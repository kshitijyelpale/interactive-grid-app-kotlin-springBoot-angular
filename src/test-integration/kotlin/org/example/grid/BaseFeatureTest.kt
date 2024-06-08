package org.example.grid

import org.example.grid.dao.repository.CellRepository
import org.example.grid.dao.repository.GridRepository
import org.example.grid.service.IGridService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.ResultSet
import java.sql.SQLException

class CustomInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {}
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = [CustomInitializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
abstract class BaseFeatureTest {

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("test_grid")
            withUsername("test")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            container.start()
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.username", container::getUsername)
        }
    }

    @Autowired
    protected lateinit var gridService: IGridService

    @Autowired
    protected lateinit var gridRepository: GridRepository

    @Autowired
    protected lateinit var cellRepository: CellRepository

    @Autowired
    protected lateinit var databaseCleaner: PostgresDatabaseCleaner

    @Autowired
    protected lateinit var transactionTemplate: TransactionTemplate

    @AfterEach
    fun tearDown() {
        databaseCleaner.truncateAll()
    }
}
