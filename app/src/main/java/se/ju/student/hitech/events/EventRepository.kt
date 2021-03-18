package se.ju.student.hitech.events

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class EventRepository {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var latestId: Int = 0

    companion object {
        val eventRepository = EventRepository()
    }

    fun addEvent(
        title: String,
        date: String,
        time: String,
        location: String,
        information: String,
        callback: (String) -> Unit
    ) {

        loadAllEventsData { result, list ->
            when (result) {
                "notFound" -> {
                    Log.d("Error fireStore", "Error loading novelty from fireStore")
                }
                "successful" -> {
                    latestId = list.last().id
                    val event = hashMapOf(
                        "title" to title,
                        "date" to date,
                        "time" to time,
                        "location" to location,
                        "information" to information,
                        "id" to latestId + 1
                    )

                    db.collection("events").document(event["id"].toString()).set(event)
                        .addOnCompleteListener {
                            callback("successful")
                        }.addOnFailureListener {
                            callback("internalError")
                        }
                }
            }
        }
    }

    fun listenForEventChanges(
        callback: (String, MutableList<Event>) -> Unit
    ) {
        db.collection("events").orderBy("id")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mutableListOf(Event()))
                } else {
                    val currentEventList = mutableListOf<Event>()
                    querySnapshot?.documents?.forEach { doc ->
                        val event = doc.toObject(Event::class.java)!!
                        currentEventList.add(event)
                    }
                    callback("successful", currentEventList)
                }
            }
    }

    private fun loadAllEventsData(
        callback: (String, MutableList<Event>) -> Unit
    ) {
        db.collection("events").orderBy("id").get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                callback("notFound", mutableListOf(Event()))
            } else {
                val currentEventList = mutableListOf<Event>()
                snapshot.documents.forEach { DocumentSnapshot ->
                    val event = DocumentSnapshot.toObject(Event::class.java)!!
                    currentEventList.add(event)
                }
                callback("successful", currentEventList)
            }

        }.addOnFailureListener { error ->
            Log.w("Get user info database error", error)
            callback("internalError", mutableListOf(Event()))
        }
    }

    fun deleteEvent(id: Int): Task<Void> {
        return db.collection("events").document(id.toString()).delete()
    }

    fun updateEvent(
        newTitle: String,
        newDate: String,
        newTime: String,
        newLocation: String,
        newInformation: String,
        id: Int
    ): Task<Void> {

        val event = hashMapOf(
            "title" to newTitle,
            "date" to newDate,
            "time" to newTime,
            "location" to newLocation,
            "information" to newInformation,
            "id" to id
        )
        return db.collection("events").document(id.toString()).set(event)
    }

    fun getEventById(id: Int, callback: (String, Event) -> Unit) {
        db.collection("events").document(id.toString()).get().addOnSuccessListener {
            val event = it.toObject(Event::class.java)
            if (event != null) {
                callback("successful", event)
            }
        }.addOnFailureListener {
            callback("internalError", Event())
        }
    }
}