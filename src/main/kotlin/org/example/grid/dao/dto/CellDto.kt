package org.example.grid.dao.dto

import jakarta.validation.constraints.Min
import org.example.grid.dao.model.ValueEffect
import java.io.Serializable

/**
 * DTO for {@link org.example.grid.dao.model.Cell}
 */
data class CellDto(
    val id: Long = 0,

    val gridId: Long,

    @field:Min(value = 1, message = "row index must be grater than 1")
    val rowIndex: Int,

    @field:Min(value = 1, message = "column index must be grater than 1")
    val columnIndex: Int,

    val value: Long? = null,

    var effect: ValueEffect? = null
) : Serializable
