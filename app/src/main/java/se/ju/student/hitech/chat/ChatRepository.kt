package se.ju.student.hitech.chat

import android.util.Log
import com.google.firebase.firestore.*
import se.ju.student.hitech.handlers.TimeHandler

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val timeHandler = TimeHandler()
    private lateinit var currentMessageListener: ListenerRegistration

    companion object{
        private var currentChatID = "noChatSelected"
    }


    fun getCurrentChatID(): String {
        return currentChatID
    }

    fun setCurrentChatID(newChatID: String){
        currentChatID = newChatID
    }

    fun createNewChat(localAndroidID: String, localUsername:String,case: String, callback: (String) -> Unit) {

        val chat = hashMapOf(
            "androidIDUser" to localAndroidID,
            "case" to case,
            "lastUpdated" to timeHandler.getLocalZoneTimestampInSeconds(),
            "activeAdmin" to "",
            "isActive" to true,
            "lastSentMsg" to "",
            "localUsername" to localUsername,
            "chatID" to ""
        )

        db.collection("chats")
            .add(chat)
            .addOnSuccessListener { documentReference ->
                val id = documentReference.id

                db.collection("chats").document(id)
                    .update("chatID", id)
                    .addOnSuccessListener {
                        callback("successful")
                    }.addOnFailureListener { error ->
                        Log.w("Create new chat error", error)
                        callback("internalError")

                    }
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
            "sentFromAdmin" to isAdmin,
            "timestamp" to timeHandler.getLocalZoneTimestampInSeconds(),
            "msgText" to msgText
        )

        db.collection("chats").document(chatID).collection("messages")
            .add(msg)
            .addOnSuccessListener {
                db.collection("chats").document(chatID)
                    .update("lastUpdated", timeHandler.getLocalZoneTimestampInSeconds(), "lastSentMsg", msgText)
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

    fun getChatIDWithAndroidID(
        androidID: String,
        callback: (String, String) -> Unit
    ) {

        db.collection("chats")
            .whereEqualTo("androidIDUser", androidID)
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
                Log.w("Get user info database error", error)
                callback("internalError", "")

            }

    }

    //Tar bort den specifika chatt meddelande lyssnaren
    fun removeCurrentSpecificChatMessagesLoader(){
        currentMessageListener.remove()

    }

    //En callback funktion som laddar in alla messages till "messagesList" och uppdaterar listan när ändringar görs i databasen
    //Första gången funktionen kallas kommer den callbacka "loaded" när den laddat in all data, efter det gör den alla uppdateringar i bakgrunden
    //Listerner behövs tas bort och anropas på nytt om användaren byter chatt
    //Den kan tas bort genom att anropa "removeCurrentSpecificChatMessagesLoader" vilket bör göras varje gång innan en chatt laddas så inte några chattar mixas av misstag
    //Hur man anropar funktionen finns under funktionen

    fun loadAllMessagesFromSpecificChatAndUpdateIfChanged(
        chatID: String,
        callback: (String,  MutableList<Message>) -> Unit
    ) {
        currentMessageListener =  db.collection("chats").document(chatID).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener {  querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mutableListOf(Message()))
                }else{
                    val currentChatMessagesList = mutableListOf<Message>()
                    querySnapshot!!.documents.forEach { doc ->
                        val message = doc.toObject(Message::class.java)!!
                        currentChatMessagesList.add(message)
                    }



                    callback("successful", currentChatMessagesList)
                }

            }

    }

    /*
    chatRepository.removeCurrentSpecificChatMessagesLoader()

    chatRepository.loadAllMessagesFromSpecificChatAndUpdateIfChanged(chatID) { result ->

        when (result) {
            "loaded" -> {

                //all data laddat, säkert att sätta in det i viewn

            }

            "internalError" -> {
                //meddela användaren om att något gick fel med att hämta/uppdatera datan
            }
        }
    }
    */

    //En callback funktion som laddar in alla aktiva chattar till "activeChatsList" och uppdaterar listan och gör en ny callback när ändringar görs i databasen
    //Hur man anropar funktionen finns under funktionen
    fun loadAllActiveChatsAndUpdateIfChanged(
        callback: (String,  MutableList<Chat>) -> Unit
    ) {
        db.collection("chats")
            .whereEqualTo("isActive", true)
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mutableListOf(Chat()))
                }else{
                    val activeChatsList = mutableListOf<Chat>()
                    querySnapshot?.documents?.forEach { doc ->
                        val chat = doc.toObject(Chat::class.java)!!
                        activeChatsList.add(chat)
                    }


                    callback("successful", activeChatsList)
                }



            }
    }
    /*
     chatRepository.loadAllActiveChatsAndUpdateIfChanged() { result ->

                when (result) {
                    "loaded" -> {

                        //all data laddat, säkert att sätta in det i viewn

                    }

                    "internalError" -> {
                        //meddela användaren om att något gick fel med att hämta/uppdatera datan
                    }
                }
            }
     */


    //En callback funktion som sätter en listener efter nya chattar och callbackar när en ny chatt läggs tills
    //Används för att skicka en notis om ny chatt
    //En "Chat" class skickas med callbacken så man kan visa vilket case det är i notisen
    //Hur man anropar funktionen finns under funktionen
    fun setNewChatListener(
        callback: (String, Chat) -> Unit,
    ) {
        var firstCall = true
        db.collection("chats")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
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

    /*
    chatRepository.setNewChatListener() { result, dataMap ->

                when (result) {
                    "newChat" -> {
                        //skicka notis
                    }
                    "internalError" -> {
                        //meddela användare
                    }
                }

            }
     */


    //En callback funktion som sätter en listener efter nya meddelande för en specifik chatt och callbackar när ett nytt medelande läggs tills
    //Används för att skicka en notis om nytt meddelande
    //En "Message" class skickas med callbacken så man kan visa meddelandet i notisen
    //Hur man anropar funktionen finns under funktionen
    fun setNewMessagesListener(
        chatID: String,
        callback: (String, Message) -> Unit,
    ) {
        var firstCall = true
        db.collection("chats").document(chatID).collection("messages")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
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
                }else{
                    firstCall = false
                }

            }
    }

    /*
      chatRepository.setNewMessagesListener(chatID) { result, message ->

                when (result) {
                    "newChat" -> {
                       //skicka notis
                    }
                    "internalError" -> {
                        //Meddela användare om fel
                    }
                }

            }
     */

}


