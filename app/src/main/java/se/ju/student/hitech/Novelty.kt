package se.ju.student.hitech

import java.sql.RowId
import java.sql.Timestamp

data class Novelty(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val image: Int = 0,
    val post_type: Long = 1,
    val date: Timestamp? = null
)