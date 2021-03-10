package se.ju.student.hitech.notifications

import se.ju.student.hitech.notifications.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String
)