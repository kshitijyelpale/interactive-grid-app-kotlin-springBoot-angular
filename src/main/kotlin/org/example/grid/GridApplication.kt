package org.example.grid

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class GridApplication

fun main(args: Array<String>) {
    runApplication<GridApplication>(*args)
}

