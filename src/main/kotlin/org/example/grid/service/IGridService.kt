package org.example.grid.service

import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto

interface IGridService {

    fun createGrid(gridDto: GridDto): GridDto
    fun getGridState(gridId: Long): GridDto
    fun createGridCell(cellDto: CellDto): List<CellDto>
    fun getGridCell(cellId: Long): CellDto
    fun updateGridCell(cellId: Long): List<CellDto>
    fun deleteGridCell(cellId: Long): Boolean
}
