package se.ju.student.hitech.events

data class Event(
    val id: Int = 0,
    val title: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val information: String = ""
)