package org.example.grid.dao.repository

import jakarta.transaction.Transactional
import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.model.Cell
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CellRepository : JpaRepository<Cell, Long> {

    fun findCellById(id: Long): CellDto?

    fun findAllByGridIdAndRowIndexOrderByColumnIndex(gridId: Long, rowIndex: Int): List<Cell>

    fun findAllByGridIdAndColumnIndexOrderByRowIndex(gridId: Long, columnIndex: Int): List<Cell>

    @Modifying
    @Transactional
    @Query(value = "UPDATE cell SET value = value + 1, time_updated = NOW() WHERE row_index = :row", nativeQuery = true)
    fun updateAllByRowIndex(@Param("row") columnIndex: Int): Int

    @Modifying
    @Transactional
    @Query(value = "UPDATE cell SET value = value + 1, time_updated = NOW() WHERE column_index = :column", nativeQuery = true)
    fun updateAllByColumnIndex(@Param("column") columnIndex: Int): Int

    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO cell (grid_id, row_index, column_index, value, time_created, time_updated) " +
                "VALUES (:gridId, :row, :column, 1, NOW(), NOW()) " +
                "ON CONFLICT (grid_id, row_index, column_index) " +
                "DO UPDATE SET value = cell.value + 1, time_updated = NOW()", nativeQuery = true
    )
    fun insertOrUpdateCell(
        @Param("gridId") gridId: Long,
        @Param("row") rowIndex: Int,
        @Param("column") columnIndex: Int
    ): Int
}
