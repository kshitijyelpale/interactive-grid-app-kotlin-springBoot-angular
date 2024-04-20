import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto
import org.example.grid.dao.model.Cell
import org.example.grid.dao.model.Grid

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

fun Cell.toDto() = CellDto(
    id = id,
    gridId = gridId,
    rowIndex = rowIndex,
    columnIndex = columnIndex,
    value = value,
    effect = effect
)

fun CellDto.toModel() = Cell(
    gridId = gridId,
    rowIndex = rowIndex,
    columnIndex = columnIndex,
    value = value?.let { it + 1 } ?: 1
)
