package se.ju.student.hitech.news

import java.sql.Timestamp

data class News(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val image: Int = 0,
    val post_type: Long = 1,
    val date: Timestamp? = null
)