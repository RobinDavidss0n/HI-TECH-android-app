package se.ju.student.hitech.chat

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import se.ju.student.hitech.handlers.TimeHandler

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val timeHandler = TimeHandler()
    private val activeChatsList = mutableListOf<Chat>()


    fun getAllActiveChats():List<Chat>{
        return activeChatsList
    }

    fun createNewChat(localAndroidID: String, case: String, callback: (String) -> Unit) {

        val chat = hashMapOf(
            "androidIDUser" to localAndroidID,
            "case" to case,
            "lastUpdated" to timeHandler.getLocalZoneTimestamp().time,
            "activeAdmin" to "",
            "isActive" to true
        )

        db.collection("chats")
            .add(chat)
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Create new chat error", error)
                callback("internalError")

            }
    }

    fun addAdminToChat(adminID: String, chatID: String, callback: (String) -> Unit) {

        db.collection("chats").document(chatID)
            .update("activeAdmin", adminID)
            .addOnSuccessListener {
                callback("successful")
            }.addOnFailureListener { error ->
                Log.w("Add user to chat database error", error)
                callback("internalError")

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
            "isAdmin" to isAdmin,
            "timestamp" to timeHandler.getLocalZoneTimestamp().time,
            "msgText" to msgText
        )

        db.collection("chats").document(chatID).collection("messages")
            .add(msg)
            .addOnSuccessListener {
                db.collection("chats").document(chatID)
                    .update("lastUpdated", timeHandler.getLocalZoneTimestamp().time)
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

    fun deactivateChat(chatID: String, callback: (String) -> Unit) {

        db.collection("chats").document(chatID)
            .update(
                "isActive", false,
                "androidIDUser", "",
                "activeAdmin", ""
            )
            .addOnSuccessListener {

            }.addOnFailureListener { error ->
                Log.w("Deactivate chat database error", error)
                callback("internalError")

            }

    }

    fun getChatWithAndroidID(
        androidID: String,
        callback: (String, Map<String, Any>) -> Unit
    ) {

        db.collection("chats")
            .whereEqualTo("androidIDUser", androidID)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    callback("notFound", mapOf())
                } else {
                    result.forEach { chat ->
                        callback("successful" ,chat.data)
                    }
                }


            }.addOnFailureListener { error ->
                Log.w("Get user info database error", error)
                callback("internalError", mapOf())

            }

    }

    fun getAllMessagesFromChat(
        chatID: String,
        callback: (String, Map<String, Any>) -> Unit
    ) {

        db.collection("chats").document(chatID).collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    callback("notFound", mapOf())
                } else {
                    result.forEach { messages ->
                        callback("successful" ,messages.data)
                    }
                }


            }.addOnFailureListener { error ->
                Log.w("Get messages database error", error)
                callback("internalError", mapOf())

            }

    }

    fun getAllActiveChats(
        callback: (String, Map<String, Any>) -> Unit
    ) {

        db.collection("chats")
            .whereEqualTo("isActive", true)
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    callback("notFound", mapOf())
                } else {
                    result.forEach { messages ->
                        callback("successful" ,messages.data)
                    }
                }


            }.addOnFailureListener { error ->
                Log.w("Get all active chats database error", error)
                callback("internalError", mapOf())

            }

    }

    fun setNewChatListener(
        callback: (String, Map<String, Any>) -> Unit,
    ) {
        var firstSetup = true
        db.collection("chats")
            .addSnapshotListener { result, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mapOf())
                }
                if (!firstSetup) {
                    if (result != null && !result.isEmpty) {
                        for (chat in result.documentChanges) {
                            when (chat.type) {
                                DocumentChange.Type.ADDED -> callback("newChat", chat.document.data)
                                else -> Log.d("chatListener", "Current data: null")
                            }
                        }

                    } else {
                        Log.d("newMessagesListener", "Current data: null")
                    }
                } else {
                    firstSetup = false
                }

            }

    }


    fun setNewMessagesListener(
        chatID: String,
        callback: (String, Map<String, Any>) -> Unit,
    ) {
        var firstSetup = true
        db.collection("chats").document(chatID).collection("messages")
            .addSnapshotListener { result, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mapOf())
                }
                if (!firstSetup) {
                    if (result != null && !result.isEmpty) {
                        for (chat in result.documentChanges) {
                            when (chat.type) {
                                DocumentChange.Type.ADDED -> callback("newChat", chat.document.data)
                                else -> Log.d("chatListener", "Current data: null")
                            }
                        }

                    } else {
                        Log.d("newMessagesListener", "Current data: null")
                    }
                } else {
                    firstSetup = false
                }


            }
    }

}


