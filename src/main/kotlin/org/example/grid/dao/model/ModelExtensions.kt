import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto
import org.example.grid.dao.model.Cell
import org.example.grid.dao.model.Grid
import org.example.grid.dao.model.ValueEffect

fun Grid.toDto() = GridDto(
    id = id,
    numberOfRows = numberOfRows,
    numberOfColumns = numberOfColumns,
    cells = cells.map { it.toDto() },
)

fun GridDto.toModel() = Grid(
    numberOfRows = numberOfRows,
    numberOfColumns = numberOfColumns
)

fun Cell.toDto(customEffect: ValueEffect? = null) = CellDto(
    id = id,
    gridId = gridId,
    rowIndex = rowIndex,
    columnIndex = columnIndex,
    value = value,
    effect = customEffect ?: effect
)

fun CellDto.toModel(customEffect: ValueEffect? = null) = Cell(
    gridId = gridId,
    rowIndex = rowIndex,
    columnIndex = columnIndex,
    value = value?.let { it + 1 } ?: 1,
    effect = customEffect
)
