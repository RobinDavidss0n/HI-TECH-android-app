package se.ju.student.hitech.handlers

import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class TimeHandler {

    fun getLocalZoneTimestampInSeconds(): Long {
        return Timestamp
            .valueOf(
                DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss.ss")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
            ).convertToLocalTime()!!.time
    }
}

fun Long.convertTimeToTimestamp(): Timestamp? {

    return Timestamp.valueOf(
        DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(java.sql.Date(this.toLong()).toInstant())
    )

}


fun Long.convertTimeToStringTimeFormat(): String? {

    return DateTimeFormatter
        .ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(java.sql.Date(this.toLong()).toInstant())
}

fun Long.convertTimeToStringHourMinutesFormat(): String? {

    return DateTimeFormatter
        .ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(java.sql.Date(this.toString().toLong()).toInstant())
}

fun Long.convertTimeToStringDateFormat(): String? {

    return DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
        .format(java.sql.Date(this.toLong()).toInstant())
}

fun Timestamp.convertToLocalTime(): Timestamp? {

    val dateFormat = "yyyy-MM-dd HH:mm:ss"

    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = TimeZone.getTimeZone("UTC")
    val date = parser.parse(this.toString())!!

    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    val localDate = formatter.format(date)

    return Timestamp.valueOf(localDate.toString())

}