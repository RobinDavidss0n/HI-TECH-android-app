package se.ju.student.hitech

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Date
import java.sql.Timestamp
import java.sql.Types.TIMESTAMP
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

var chatRepository = ChatRepository()

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createNewChat(chatOpenerID: String, subject: String, callback: (String, String) -> Unit){

        val chat = hashMapOf(
            "chatOpenerID" to chatOpenerID,
            "subject" to  subject,
            "lastUpdated" to  getCurrentTimestamp()
        )

        db.collection("chat")
            .add(chat)
            .addOnSuccessListener { document ->
                callback("successful", document.id.toString())
            }.addOnFailureListener{ error ->
                Log.d("Insert user into database error", error.toString())
                callback("internalError", "")

            }
    }

    fun getCurrentTimestamp(): Timestamp {
        return Timestamp( DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now()).toLong())
    }

    fun getDateFromTimestamp(timestamp: Timestamp): String? {
        return DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Date(timestamp.time).toInstant())
    }
}