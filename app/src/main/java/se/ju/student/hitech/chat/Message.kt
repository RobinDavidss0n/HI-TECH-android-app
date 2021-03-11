package se.ju.student.hitech.chat

import java.sql.Timestamp

data class Message(
    var isAdmin: String? = null,
    var timestamp: Long? = null,
    var msgText: String? = null
)
