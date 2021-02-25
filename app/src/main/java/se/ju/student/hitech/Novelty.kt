package se.ju.student.hitech

import java.sql.Timestamp

data class Novelty(
    val title: String = "",
    val content: String = "",
    val image: Int = 0,
    val post_type: Long = 0,
    val date: Timestamp? = null
)