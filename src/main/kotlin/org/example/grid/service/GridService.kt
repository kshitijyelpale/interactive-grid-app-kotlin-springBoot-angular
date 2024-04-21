package org.example.grid.service

import com.example.grid.exception.NoSuchElementFoundException
import com.example.grid.exception.ServiceException
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto
import org.example.grid.dao.model.Cell
import org.example.grid.dao.model.Grid
import org.example.grid.dao.model.ValueEffect
import org.example.grid.dao.repository.CellRepository
import org.example.grid.dao.repository.GridRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import toDto
import toModel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
@Transactional
internal class GridService(
    private val gridRepository: GridRepository,
    private val cellRepository: CellRepository,
    private val transactionTemplate: TransactionTemplate
) : IGridService {
    private val accessLock = ReentrantLock()

    private val logger = KotlinLogging.logger {}

    companion object {
        const val INITIAL_VALUE = 1L
        const val INCREMENT_VALUE = 1L
        const val FIBONACCI_LENGTH = 5
    }

    override fun createGrid(gridDto: GridDto): GridDto {
        logger.info("Creating a new grid of size ${gridDto.numberOfRows} x ${gridDto.numberOfColumns}")
        return gridRepository.save(gridDto.toModel()).toDto()
    }

    override fun findAllGrids(): List<GridDto> {
        return gridRepository.findAll().map { it.toDto() }
    }

    override fun getGridState(gridId: Long): GridDto {
        logger.info("Finding a grid $gridId")
        return gridRepository.findByIdOrNull(gridId)?.toDto() ?: throw NoSuchElementFoundException("Grid not found")
    }

    override fun createGridCell(cellDto: CellDto): List<CellDto> {
        logger.info("Creating a grid cell for grid: ${cellDto.gridId}, row: ${cellDto.rowIndex}, column: ${cellDto.columnIndex}")
       return accessLock.withLock {
            transactionTemplate.execute {
                val updatedCells: MutableList<CellDto> = mutableListOf()
                // create clicked cell
                val cell = cellRepository.save(cellDto.toModel(ValueEffect.YELLOW))
                updatedCells.add(cell.toDto())
                updatedCells.addAll(upsertCells(cell))

                updatedCells
            } ?: throw IllegalStateException("Transaction execution failed for creating grid cell ${cellDto.rowIndex} x ${cellDto.columnIndex}.")
        }
    }

    override fun getGridCell(cellId: Long): CellDto {
        logger.info("Finding a grid cell $cellId")
        return cellRepository.findCellById(cellId) ?: throw NoSuchElementFoundException("Cell not found")
    }

    override fun updateGridCell(cellId: Long): List<CellDto> {
        logger.info("Updating a grid cell $cellId")
        return accessLock.withLock {
            transactionTemplate.execute {
                val updatedCells: MutableList<CellDto> = mutableListOf()
                // update clicked cell
                val cell = cellRepository.findById(cellId).get()
                updatedCells.add(
                    cellRepository.save(cell.copy(value = cell.value + INCREMENT_VALUE, effect = ValueEffect.YELLOW))
                        .toDto()
                )
                updatedCells.addAll(upsertCells(cell))

                updatedCells
            } ?: throw IllegalStateException("Transaction execution failed for updating grid cell $cellId.")
        }
    }

    override fun deleteGridCell(cellId: Long): Boolean {
        logger.info("Deleting a grid cell $cellId")
        return try {
            cellRepository.deleteById(cellId)
            true
        } catch (e: NoSuchElementException) {
            throw NoSuchElementFoundException("Cell $cellId not found: ${e.message}")
        } catch (e: Exception) {
            throw ServiceException("Cell $cellId failed: ${e.message}")
        }
    }

    private fun upsertCells(cell: Cell): Set<CellDto> {
        try {
            val grid = gridRepository.findById(cell.gridId).get()

            val missingCells = createOrUpdateRowCell(grid, cell)

            missingCells.addAll(createOrUpdateColumnsCells(grid, cell))

            val saveAll = cellRepository.saveAll(missingCells)
            val cellDtos = saveAll.map { it.toDto() }.toMutableSet()

            cellDtos.addAll(processCells(grid.id, numberOfRows = grid.numberOfRows))
            cellDtos.addAll(processCells(grid.id, numberOfColumns = grid.numberOfColumns))

            return cellDtos
        } catch (e: Exception) {
            throw ServiceException("Error saving rows and columns for a requested cell: ${cell} with error: ${e.message}")
        }
    }

    private fun createOrUpdateColumnsCells(grid: Grid, cell: Cell, ): MutableList<Cell>  {
        val existingColumnCells = cellRepository.findAllByGridIdAndRowIndexOrderByColumnIndex(grid.id, cell.rowIndex)
        val maxColumns = grid.numberOfColumns
        val existingColumns = existingColumnCells.map { it.columnIndex }.toSet()
        val missingCells = (1..maxColumns).filter { it !in existingColumns }.map { column ->
            Cell(
                gridId = grid.id,
                rowIndex = cell.rowIndex,
                columnIndex = column,
                value = INITIAL_VALUE,
                effect = ValueEffect.YELLOW
            )
        }.toMutableList()
        // Update existing column cells
        missingCells.addAll(
            existingColumnCells.filter { it.id != cell.id }
                .map { it.copy(value = it.value + INCREMENT_VALUE, effect = ValueEffect.YELLOW) }
        )
        return missingCells
    }

    private fun createOrUpdateRowCell(grid: Grid, cell: Cell): MutableList<Cell> {
        val existingRowCells = cellRepository.findAllByGridIdAndColumnIndexOrderByRowIndex(grid.id, cell.columnIndex)
        val maxRows = grid.numberOfRows
        val existingRows = existingRowCells.map { it.rowIndex }.toSet()
        val missingCells = (1..maxRows).filter { it !in existingRows }.map { row ->
            Cell(
                gridId = grid.id,
                rowIndex = row,
                columnIndex = cell.columnIndex,
                value = INITIAL_VALUE,
                effect = ValueEffect.YELLOW
            )
        }.toMutableList()
        // Update existing row cells
        missingCells.addAll(
            existingRowCells.filter { it.id != cell.id }
                .map { it.copy(value = it.value + INCREMENT_VALUE, effect = ValueEffect.YELLOW) }
        )
        return missingCells
    }

    fun processCells(gridId: Long, numberOfRows: Int? = null, numberOfColumns: Int? = null): Set<CellDto> {
        if (numberOfRows == null && numberOfColumns == null) {
            return emptySet()
        }

        val numberParallelExecutions = numberOfRows ?: numberOfColumns ?: 0
        val syncPointTasksFinished = CountDownLatch(numberParallelExecutions)
        val executor = Executors.newFixedThreadPool(numberParallelExecutions)
        val updatedCellDtos = mutableSetOf<CellDto>()
        logger.info { """
            Processing cells in parallel execution for 
            ${if (numberOfRows != null) "${numberOfRows} rows" else "${numberOfColumns} columns"}  
           """.trimIndent()
        }
        for (i in 1..numberParallelExecutions) {
            executor.execute {
                try {
                    transactionTemplate.execute {
                        updatedCellDtos.addAll(
                            if (numberOfRows != null) {
                                checkFibonacciSequenceForRow(gridId, i)
                            }
                            else{
                                checkFibonacciSequenceForColumn(gridId, i)
                            }
                        )
                    }
                } catch (ex: Exception) {
                    logger.info { "Check for Fibonacci Sequence failed for row $i: ${ex.message}" }
                } finally {
                    syncPointTasksFinished.countDown()
                }
            }
        }
        syncPointTasksFinished.await()
        executor.shutdown()
        return updatedCellDtos
    }

    fun checkFibonacciSequenceForRow(gridId: Long, rowIndex: Int): Set<CellDto> {
        logger.info { "Check for Fibonacci Sequence for Grid $gridId and rowIndex $rowIndex" }
        val cells = cellRepository.findAllByGridIdAndRowIndexOrderByColumnIndex(gridId, rowIndex)
        val map = cells.associate { it.id to it.value }
        val fiboIds = checkFibonacciSequence(map)
        return cells.asSequence().filter { it.id in fiboIds }.map { it.toDto(customEffect = ValueEffect.GREEN) }
            .toSet()
    }

    fun checkFibonacciSequenceForColumn(gridId: Long, columnIndex: Int): Set<CellDto> {
        logger.info { "Check for Fibonacci Sequence for Grid $gridId and columnIndex $columnIndex" }
        val cells = cellRepository.findAllByGridIdAndColumnIndexOrderByRowIndex(gridId, columnIndex)
        val map = cells.associate { it.id to it.value }
        val fiboIds = checkFibonacciSequence(map)
        return cells.asSequence().filter { it.id in fiboIds }.map { it.toDto(customEffect = ValueEffect.GREEN) }
            .toSet()
    }

    fun checkFibonacciSequence(cellsMap: Map<Long, Long>): Set<Long> {
        val entries = cellsMap.entries.toList()
        val values = entries.map { it.value }
        val ids = entries.map { it.key }

        if (values.size < 5) return emptySet()  // Early return

        var fiboCount = 2
        val resultIds = mutableSetOf<Long>()
        // Start from the third item and check for Fibonacci sequence
        for (i in 2 until values.size) {
            if (values[i] == values[i - 1] + values[i - 2]) {
                fiboCount += 1
                if (fiboCount >= FIBONACCI_LENGTH) {
                    resultIds.addAll(ids.subList(i - FIBONACCI_LENGTH, i + 1))
                }
            } else {
                // Reset count if the sequence breaks
                fiboCount = 2
            }
        }

        return resultIds
    }
}
