package org.example.grid.dao.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.time.Instant

@MappedSuperclass
abstract class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    var timeCreated: Timestamp = Timestamp.from(Instant.now())

    @UpdateTimestamp
    @Column(nullable = true)
    val timeUpdated: Timestamp? = null
}
