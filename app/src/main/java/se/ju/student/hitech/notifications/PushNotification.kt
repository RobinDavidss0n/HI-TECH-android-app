package se.ju.student.hitech.notifications

data class PushNotification(
    val data: NotificationData,
    val to: String
)