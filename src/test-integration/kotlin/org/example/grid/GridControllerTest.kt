package org.example.grid

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto
import org.example.grid.dao.model.Grid
import org.example.grid.dao.model.ValueEffect
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import toModel
import kotlin.jvm.optionals.getOrNull

class GridControllerTest : BaseFeatureTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val api = "/grid"

    private lateinit var savedGrid: Grid

    @BeforeEach
    fun setup() {
        val grid = GridDto(numberOfRows = 50, numberOfColumns = 50)
        savedGrid = gridRepository.save(grid.toModel())
        val cells = (3..9).map {
            CellDto(
                gridId = savedGrid.id,
                rowIndex = it,
                columnIndex = it + 2
            ).toModel()
        }
        cellRepository.saveAll(cells)
    }

    @Test
    fun `should create grid`() {
        val grid = GridDto(numberOfRows = 20, numberOfColumns = 70)
        mockMvc.perform(
            post(api)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(grid))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.numberOfRows").value(20))
            .andExpect(jsonPath("$.numberOfColumns").value(70))
            .andExpect(jsonPath("$.cells.size()").value(0))
    }

    @Test
    fun `should get created grid`() {
        mockMvc.perform(get("$api/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.numberOfRows").value(50))
            .andExpect(jsonPath("$.numberOfColumns").value(50))
    }

    @Test
    fun `should fetch all existing grids`() {
        val grid = GridDto(numberOfRows = 100, numberOfColumns = 100)
        gridRepository.save(grid.toModel())
        mockMvc.perform(get(api))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].numberOfRows").value(100))
            .andExpect(jsonPath("$[1].numberOfColumns").value(100))
    }

    @Test
    fun `should create cell for grid id 1`() {
        // given
        val newCell = CellDto(
            gridId = savedGrid.id,
            rowIndex = 10,
            columnIndex = 12
        )
        // when
        val mvcResult: MvcResult = mockMvc.perform(post("$api/cell")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newCell)))
            .andExpect(status().isCreated)
            .andReturn()

        // then
        val responseString = mvcResult.response.contentAsString
        val cells: List<CellDto> = objectMapper.readValue(responseString, object : TypeReference<List<CellDto>>() {})
        assertEquals(99, cells.size, "The size of the returned array should be 99")
        val insertedCell = cells.filter { it.rowIndex == 10 && it.columnIndex == 12 }[0]
        assertNotEquals(0, insertedCell.id)
        assertEquals(ValueEffect.YELLOW, insertedCell.effect)
    }

    @Test
    fun `should update cell for grid id 1`() {
        // given
        val cellsInRow4 = cellRepository.findAllByGridIdAndRowIndexOrderByColumnIndex(savedGrid.id, rowIndex = 4)
        val cellIdToUpdate = cellsInRow4.first()
        // when
        val mvcResult: MvcResult = mockMvc.perform(put("$api/cell/${cellIdToUpdate.id}"))
            .andExpect(status().isOk)
            .andReturn()

        // then
        val responseString = mvcResult.response.contentAsString
        val cells: List<CellDto> = objectMapper.readValue(responseString, object : TypeReference<List<CellDto>>() {})
        assertEquals(99, cells.size, "The size of the returned array should be 99")
        val updatedCell = cells.filter { it.rowIndex == cellIdToUpdate.rowIndex && it.columnIndex == cellIdToUpdate.columnIndex }[0]
        assertEquals(cellIdToUpdate.id, updatedCell.id)
        assertEquals(cellIdToUpdate.rowIndex, updatedCell.rowIndex)
        assertEquals(cellIdToUpdate.columnIndex, updatedCell.columnIndex)
        assertEquals(cellIdToUpdate.value + 1, updatedCell.value)
        assertEquals(ValueEffect.YELLOW, updatedCell.effect)
        assertEquals(50, cells.filter { it.rowIndex == cellIdToUpdate.rowIndex }.size)
        assertEquals(50, cells.filter { it.columnIndex == cellIdToUpdate.columnIndex }.size)
    }

    @Test
    fun `should delete a cell for grid id 1`() {
        // given
        val cellsInColumn5 = cellRepository.findAllByGridIdAndColumnIndexOrderByRowIndex(savedGrid.id, 5)
        val cellIdToDelete = cellsInColumn5.first()
        //when/then
        mockMvc.perform(delete("$api/cell/${cellIdToDelete.id}"))
            .andExpect(status().isOk)
            .andExpect(content().string("true"))
        assertNull(cellRepository.findById(cellIdToDelete.id).getOrNull())

    }
}
