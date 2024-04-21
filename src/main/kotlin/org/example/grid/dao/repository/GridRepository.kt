package org.example.grid.dao.repository

import org.example.grid.dao.model.Grid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GridRepository : JpaRepository<Grid, Long>
