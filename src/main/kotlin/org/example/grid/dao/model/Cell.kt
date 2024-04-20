package org.example.grid.dao.model

import jakarta.persistence.*

@Entity
@Table(
    name = "cell", indexes = [
        Index(name = "cell_row_index", columnList = "row_index"),
        Index(name = "cell_column_index", columnList = "column_index")
    ]
)
data class Cell(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,   // will be replaced by hibernate

    @Column(nullable = false)
    val gridId: Long,

    @Column(nullable = false)
    val rowIndex: Int,

    @Column(nullable = false)
    val columnIndex: Int,

    val value: Long,

    val effect: ValueEffect? = null,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        Long.MAX_VALUE

        other as Cell

        if (id != other.id) return false
        if (rowIndex != other.rowIndex) return false
        if (columnIndex != other.columnIndex) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + rowIndex
        result = 31 * result + columnIndex
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String {
        return "Cell(id=$id, rowIndex=$rowIndex, columnIndex=$columnIndex, value=$value)"
    }
}
