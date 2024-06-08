package org.example.grid.service

import io.mockk.mockk
import org.example.grid.dao.repository.CellRepository
import org.example.grid.dao.repository.GridRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.transaction.support.TransactionTemplate

class GridServiceTest {

    private val sut = GridService(
        mockk<GridRepository>(),
        mockk<CellRepository>(),
        mockk<TransactionTemplate>()
    )

    @Test
    fun `test with simple Fibonacci sequence`() {
        val cellsMap = mapOf(
            1L to 1L,
            2L to 1L,
            3L to 2L,
            4L to 3L,
            5L to 5L,
            6L to 8L
        )
        val expected = setOf(1L, 2L, 3L, 4L, 5L, 6L)
        assertEquals(expected, sut.checkFibonacciSequence(cellsMap))
    }

    @Test
    fun `test without Fibonacci sequence`() {
        val cellsMap = mapOf(
            1L to 1L,
            2L to 4L,
            3L to 3L,
            4L to 7L,
            5L to 11L
        )
        assertTrue(sut.checkFibonacciSequence(cellsMap).isEmpty())
    }

    @Test
    fun `test with less than minimum Fibonacci length`() {
        val cellsMap = mapOf(
            1L to 1L,
            2L to 1L,
            3L to 2L,
            4L to 3L
        )
        assertTrue(sut.checkFibonacciSequence(cellsMap).isEmpty())
    }

    @Test
    fun `test with more than 1 Fibonacci sequences`() {
        val cellsMap = mapOf(
            15434L to 1L,
            1564354L to 1L,
            1754L to 2L,
            1565L to 1L,
            2654L to 1L,
            3654L to 2L,
            4654L to 3L,
            5654L to 1L,
            665454L to 1L,
            7546L to 34,
            8432L to 55L,
            943125L to 89L,
            2432L to 144L,
            3534L to 233L,
            4635L to 377L,
            1432L to 610L,
            2543L to 1L,
            3423L to 2L,
            4634L to 3L,
            14312L to 1L,
            2653L to 1L,
            34123L to 2L,
            44535L to 3L,
            15346L to 5L,
            2234L to 8L,
            34235L to 13L,
            45634L to 21L,
            435L to 33L,
        )
        val expected = setOf(7546L, 8432L, 943125L, 2432L, 3534L, 4635L, 1432L, 14312L, 2653L, 34123L, 44535L, 15346L, 2234L, 34235L, 45634L)
        assertEquals(expected, sut.checkFibonacciSequence(cellsMap))
    }
}
