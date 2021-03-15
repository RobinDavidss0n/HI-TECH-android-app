package se.ju.student.hitech.chat

import java.sql.Timestamp

data class Chat (
    var androidIDUser: String? = null,
    var localUsername: String? = null,
    var case: String? = null,
    var lastUpdated: Long? = null,
    var activeAdmin: String? = null,
    var isActive: Boolean? = null,
    var chatID: String? = null
)

