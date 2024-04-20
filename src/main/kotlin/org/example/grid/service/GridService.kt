package org.example.grid.service

import com.example.todoapp.exception.NoSuchElementFoundException
import com.example.todoapp.exception.ServiceException
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto
import org.example.grid.dao.repository.CellRepository
import org.example.grid.dao.repository.GridRepository
import org.springframework.stereotype.Service
import toDto
import toModel
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
@Transactional
internal class GridService(
    private val gridRepository: GridRepository,
    private val cellRepository: CellRepository
) : IGridService {
    private val accessLock = ReentrantLock()

    private val logger = KotlinLogging.logger {}

    override fun createGrid(gridDto: GridDto): GridDto {
        logger.info("Creating a new grid of size ${gridDto.numberOfRows} x ${gridDto.numberOfColumns}")
        return gridRepository.save(gridDto.toModel()).toDto()
    }

    override fun getGridState(gridId: Long): GridDto {
        logger.info("Finding a grid $gridId")
        return gridRepository.findGridById(gridId) ?:
            throw NoSuchElementFoundException("Grid not found")
    }

    override fun createGridCell(cellDto: CellDto): List<CellDto> {
        logger.info("Creating a grid cell for grid: ${cellDto.gridId}, row: ${cellDto.rowIndex}, column: ${cellDto.columnIndex}")
        accessLock.withLock {
            // update row and column cells
            return listOf(cellRepository.save(cellDto.toModel()).toDto())
        }
    }

    override fun getGridCell(cellId: Long): CellDto {
        logger.info("Finding a grid cell $cellId")
        return cellRepository.findCellById(cellId)
            ?: throw NoSuchElementFoundException("Cell not found")
    }

    override fun updateGridCell(cellId: Long): List<CellDto> {
        logger.info("Updating a grid cell $cellId")
        accessLock.withLock {
            // update row and column cells
            val cell = cellRepository.findById(cellId).get()
            val cellValue = cell.value + 1
            return listOf(cellRepository.save(cell.copy(value = cellValue)).toDto())
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
}
