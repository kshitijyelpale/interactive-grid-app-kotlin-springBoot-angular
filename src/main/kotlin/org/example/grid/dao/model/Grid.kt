package org.example.grid.dao.model

import jakarta.persistence.*

@Entity
@Table(name = "grid")
data class Grid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,   // will be replaced by hibernate

    @Column(nullable = false)
    val numberOfRows: Int,

    @Column(nullable = false)
    val numberOfColumns: Int,

    @OneToMany(mappedBy = "gridId")
    val cells: List<Cell> = mutableListOf()
): BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Grid

        if (id != other.id) return false
        if (numberOfRows != other.numberOfRows) return false
        if (numberOfColumns != other.numberOfColumns) return false
        if (numberOfColumns != other.numberOfColumns) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + numberOfRows
        result = 31 * result + numberOfColumns
        return result
    }

    override fun toString(): String {
        return "Grid(id=$id, rows=$numberOfRows, columns=$numberOfColumns)"
    }
}
