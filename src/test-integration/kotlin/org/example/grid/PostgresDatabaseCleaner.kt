package org.example.grid

import jakarta.persistence.EntityManager
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

@Component
class PostgresDatabaseCleaner {
    private val logger = KotlinLogging.logger {}
    private var dataSource: DataSource? = null
    private var entityManager: EntityManager? = null
    private var tables: ArrayList<String>? = null
    private val excludes = HashSet<String>()

    @Autowired
    fun DatabaseCleaner(dataSource: DataSource?, entityManager: EntityManager?) {
        this.dataSource = dataSource
        this.entityManager = entityManager
        addExclude("schema_version")
        addExclude("flyway_schema_history")
    }

    fun addExclude(table: String) {
        excludes.add(table.lowercase())
    }

    fun truncateAll() {
        try {
            dataSource!!.connection.use { con ->
                if (tables == null) {
                    con.createStatement().use { statement ->
                        tables = ArrayList()
                        val rs: ResultSet =
                            statement.executeQuery(
                                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;")
                        while (rs.next()) {
                            val name = rs.getString(1)
                            if (!excludes.contains(name.lowercase())) {
                                tables!!.add(name)
                            }
                        }
                        rs.close()
                    }
                }
                if (tables!!.isNotEmpty()) {
                    val sb = StringBuilder()
                    sb.append("TRUNCATE TABLE ")
                    sb.append(tables!![0])
                    for (i in 1 until tables!!.size) {
                        sb.append(',')
                        sb.append(tables!![i])
                    }
                    sb.append(" RESTART IDENTITY;")
                    con.createStatement().use { statement -> statement.executeUpdate(sb.toString()) }
                }
            }
        } catch (sqlException: SQLException) {
            logger.error("Database cleaner faced an SQL exception: ", sqlException)
        }
        entityManager!!.clear()
    }
}
