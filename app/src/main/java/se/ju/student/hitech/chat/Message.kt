package se.ju.student.hitech.chat

data class Message(
    var sentFromAdmin: Boolean = false,
    var timestamp: Long? = null,
    var msgText: String? = null
)
