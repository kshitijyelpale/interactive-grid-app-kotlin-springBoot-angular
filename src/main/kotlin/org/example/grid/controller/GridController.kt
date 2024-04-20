package org.example.grid.controller

import jakarta.validation.Valid
import org.example.grid.dao.dto.CellDto
import org.example.grid.dao.dto.GridDto
import org.example.grid.service.IGridService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/grid")
@Validated
class GridController(private val gridService: IGridService) {

    @PostMapping
    fun createGrid(@Valid @RequestBody gridDto: GridDto) = ResponseEntity(
        gridService.createGrid(gridDto), HttpStatus.CREATED
    )

    @GetMapping("/{id}")
    fun getGridState(@PathVariable(name = "id") gridId: Long) = ResponseEntity.ok(gridService.getGridState(gridId))

    @PostMapping("/cell")
    fun createCell(@Valid @RequestBody cellDto: CellDto) = ResponseEntity(
        gridService.createGridCell(cellDto), HttpStatus.CREATED
    )

    @GetMapping("/cell/{id}")
    fun getCellState(@PathVariable(name = "id") cellId: Long) = ResponseEntity.ok(gridService.getGridCell(cellId))


    @PutMapping("/cell/{id}")
    fun updateCell(@PathVariable(name = "id") cellId: Long) = ResponseEntity.ok(gridService.updateGridCell(cellId))

    @DeleteMapping("/cell/{id}")
    fun deleteCell(@PathVariable(name = "id") cellId: Long) = ResponseEntity.ok(gridService.deleteGridCell(cellId))
}
