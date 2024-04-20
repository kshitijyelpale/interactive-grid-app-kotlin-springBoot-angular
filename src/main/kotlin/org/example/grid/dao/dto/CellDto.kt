package org.example.grid.dao.dto

import jakarta.validation.constraints.PositiveOrZero
import org.example.grid.dao.model.ValueEffect
import java.io.Serializable

/**
 * DTO for {@link org.example.grid.dao.model.Cell}
 */
data class CellDto(
    val id: Long = 0,

    val gridId: Long,

    @field:PositiveOrZero
    val rowIndex: Int,

    @field:PositiveOrZero val
    columnIndex: Int,

    val value: Long? = null,

    val effect: ValueEffect? = null
) : Serializable
