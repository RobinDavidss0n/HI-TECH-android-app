package se.ju.student.hitech.chat

import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.handlers.TimeHandler

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val firebaseInstallations = FirebaseInstallations.getInstance()
    private val TOPIC_NEW_CHAT = "/topics/newChat"
    private val timeHandler = TimeHandler()
    private lateinit var currentMessageListener: ListenerRegistration

    companion object {
        private var currentChatID = "noChatSelected"
        var chatRepository = ChatRepository()
    }


    fun getCurrentChatID(): String {
        return currentChatID
    }

    fun setCurrentChatID(newChatID: String) {
        currentChatID = newChatID
    }

    fun subscribeToNewChatNotifications(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEW_CHAT)
            .addOnCompleteListener {
                callback("successful")
            }
            .addOnFailureListener { error->
                callback("internalError")
                Log.w("Create new chat error", error)

            }
    }

    fun unsubscribeToChatNotifications(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_NEW_CHAT)
            .addOnCompleteListener {
                callback("successful")
            }
            .addOnFailureListener { error->
                callback("internalError")
                Log.w("Create new chat error", error)

            }
    }

    fun createNewChatNotification(title: String, message: String) {
        MainActivity().createNotification(title, message, TOPIC_NEW_CHAT)
    }

    fun getFirebaseInstallationsID(callback: (String, String) -> Unit) {
        firebaseInstallations.id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    callback("successful", task.result!!)
                } else {
                    callback("internalError", "")
                }
            } else {
                callback("internalError", "")
                Log.e("Installations", "Unable to get Installation ID")
            }
        }
    }

    fun createNewChat(
        localID: String,
        localUsername: String,
        case: String,
        callback: (String, String) -> Unit
    ) {

        val chat = hashMapOf(
            "localID" to localID,
            "case" to case,
            "lastUpdated" to timeHandler.getLocalZoneTimestampInSeconds(),
            "activeAdmin" to "",
            "lastSentMsg" to "",
            "localUsername" to localUsername,
            "chatID" to "",
            "adminUsername" to ""
        )

        db.collection("chats")
            .add(chat)
            .addOnSuccessListener { documentReference ->
                val id = documentReference.id

                db.collection("chats").document(id)
                    .update("chatID", id)
                    .addOnSuccessListener {
                        callback("successful", id)
                    }.addOnFailureListener { error ->
                        Log.w("Create new chat error", error)
                        callback("internalError", "")
                    }
            }.addOnFailureListener { error ->
                Log.w("Create new chat error", error)
                callback("internalError", "")
            }
    }

    fun addAdminToChat(
        adminID: String,
        adminUsername: String,
        chatID: String,
        callback: (String) -> Unit
    ) {
        db.collection("chats").document(chatID)
            .update("activeAdmin", adminID, "adminUsername", adminUsername)
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Add admin to chat database error", error)
                callback("internalError")
            }
    }

    fun isChatOccupied(chatID: String, callback: (String) -> Unit) {
        db.collection("chats").document(chatID)
            .get()
            .addOnSuccessListener { docSnap ->
                val activeAdmin = docSnap.get("activeAdmin")
                if (activeAdmin == "") {
                    callback("false")
                } else {
                    callback("true")
                }

            }.addOnFailureListener { error ->
                Log.w("Check if chat is occupied database error", error)
                callback("internalError")
            }
    }

    fun checkIfCurrentAdminIsInChatOrIfEmpty(
        adminID: String,
        chatID: String,
        callback: (String) -> Unit
    ) {

        db.collection("chats").document(chatID)
            .get()
            .addOnSuccessListener { docSnap ->
                when (docSnap.get("activeAdmin")) {
                    adminID -> {
                        callback("true")
                    }
                    "" -> {
                        callback("empty")
                    }
                    else -> {
                        callback("false")
                    }
                }

            }.addOnFailureListener { error ->
                Log.w("Check if current admin is in chat or empty database error", error)
                callback("internalError")
            }
    }

    fun getChatWithChatID(chatID: String, callback: (String, Chat) -> Unit) {
        db.collection("chats").document(chatID)
            .get()
            .addOnSuccessListener { docSnap ->
                if (docSnap.exists()) {
                    val chat = docSnap.toObject(Chat::class.java)
                    if (chat != null) {
                        callback("successful", chat)
                    }
                } else {
                    callback("notFound", Chat())
                }
            }.addOnFailureListener { error ->
                Log.w("Get chat with id database error", error)
                callback("internalError", Chat())
            }
    }

    fun removeAdminFromChat(chatID: String, callback: (String) -> Unit) {
        db.collection("chats").document(chatID)
            .update("activeAdmin", "")
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Remove user from chat database error", error)
                callback("internalError")
            }
    }


    fun addMessage(msgText: String, isAdmin: Boolean, chatID: String, callback: (String) -> Unit) {
        val msg = hashMapOf(
            "sentFromAdmin" to isAdmin,
            "timestamp" to timeHandler.getLocalZoneTimestampInSeconds(),
            "msgText" to msgText
        )

        db.collection("chats").document(chatID).collection("messages")
            .add(msg)
            .addOnSuccessListener {
                db.collection("chats").document(chatID)
                    .update(
                        "lastUpdated",
                        timeHandler.getLocalZoneTimestampInSeconds(),
                        "lastSentMsg",
                        msgText
                    )
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener { error ->
                        Log.w("Update  lastUpdated in chat error", error)
                        callback("internalError")
                    }
            }.addOnFailureListener { error ->
                Log.w("Send message error", error)
                callback("internalError")
            }
    }

    fun closeChat(chatID: String, callback: (String) -> Unit) {
        db.collection("chats").document(chatID)
            .delete()
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Deleting chat database error", error)
                callback("internalError")
            }
    }

    fun getChatIDWithLocalID(
        localID: String,
        callback: (String, String) -> Unit
    ) {

        db.collection("chats")
            .whereEqualTo("localID", localID)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    callback("notFound", "")
                } else {
                    snapshot.documents.forEach { docSnap ->
                        callback("successful", docSnap.id)
                    }
                }
            }.addOnFailureListener { error ->
                Log.w("Get chat database error", error)
                callback("internalError", "")
            }
    }

    fun removeCurrentSpecificChatMessagesLoader() {
        if (this::currentMessageListener.isInitialized) {
            currentMessageListener.remove()
        }
    }

    fun loadAllMessagesFromSpecificChatAndUpdateIfChanged(
        chatID: String,
        callback: (String, MutableList<Message>, Message) -> Unit
    ) {
        var firstCall = true
        currentMessageListener = db.collection("chats").document(chatID).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mutableListOf(Message()), Message())
                } else {
                    if (firstCall) {
                        firstCall = false
                        val currentChatMessagesList = mutableListOf<Message>()
                        querySnapshot!!.documents.forEach { doc ->
                            val message = doc.toObject(Message::class.java)
                            if (message != null) {
                                currentChatMessagesList.add(message)
                            }
                        }
                        callback("firstSetup", currentChatMessagesList, Message())

                    } else {
                        querySnapshot!!.documentChanges.forEach { dc ->
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val message = dc.document.toObject(Message::class.java)
                                    callback("newData", mutableListOf(Message()), message)
                                }
                                else -> Log.d(
                                    "Message update",
                                    "Message update but not a new message."
                                )
                            }
                        }
                    }
                }
            }
    }


    fun loadAllChatsAndUpdateIfChanged(
        callback: (String, MutableList<Chat>) -> Unit
    ) {

        db.collection("chats")
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Chat listener error ", error)
                    callback("internalError", mutableListOf(Chat()))
                } else {

                    val chatsList = mutableListOf<Chat>()
                    querySnapshot!!.documents.forEach { doc ->
                        val chat = doc.toObject(Chat::class.java)
                        if (chat != null) {
                            chatsList.add(chat)
                        }
                    }
                    callback("successful", chatsList)
                }
            }
    }

    fun setNewChatNotificationListener(
        callback: (String, Chat) -> Unit,
    ) {
        var firstCall = true
        db.collection("chats")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Chat notification listener error ", error)
                    callback("internalError", Chat())
                }

                if (!firstCall) {
                    if (querySnapshot != null && !querySnapshot.isEmpty) {

                        querySnapshot.documentChanges.forEach { dc ->
                            val chat = dc.document.toObject(Chat::class.java)
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> callback("newChat", chat)
                                else -> Log.d("chatListener", "Updated but no new chats")
                            }
                        }
                    }
                } else {
                    firstCall = false
                }
            }
    }

    fun setNewMessagesNotificationListener(
        chatID: String,
        callback: (String, Message) -> Unit,
    ) {
        var firstCall = true
        db.collection("chats").document(chatID).collection("messages")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages notification listener error ", error)
                    callback("internalError", Message())
                }

                if (!firstCall) {
                    if (querySnapshot != null && !querySnapshot.isEmpty) {

                        querySnapshot.documentChanges.forEach { dc ->
                            val message = dc.document.toObject(Message::class.java)
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> callback("newMessage", message)
                                else -> Log.d("chatListener", "Updated but no new messages")
                            }
                        }
                    }
                } else {
                    firstCall = false
                }
            }
    }
}

