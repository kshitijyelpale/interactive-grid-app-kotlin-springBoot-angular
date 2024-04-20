package org.example.grid.dao.dto

import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.io.Serializable

/**
 * DTO for {@link org.example.grid.dao.model.Grid}
 */
data class GridDto(
    val id: Long = 0,

    @field:Min(value = 1, message = "Grid should have at least 1 row")
    @field:Max(value = 10000, message = "Grid should have max 10000 rows")
    val numberOfRows: Int,

    @field:Min(value = 1, message = "Grid should have at least column")
    @field:Max(value = 10000, message = "Grid should have max 10000 columns")
    val numberOfColumns: Int,

    val cells: List<CellDto> = mutableListOf()
) : Serializable
