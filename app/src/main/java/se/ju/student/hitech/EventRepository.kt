package se.ju.student.hitech

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

var eventRepository = EventRepository()

class EventRepository {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var eventList = mutableListOf<Event>()

    fun addEvent(title: String, date: String, time: String, location: String, information: String) {

        val id = when {
            eventList.count() == 0 -> 1
            else -> eventList.first().id + 1
        }

        val event = hashMapOf(
            "title" to title,
            "date" to date,
            "time" to time,
            "location" to location,
            "information" to information,
            "id" to id
        )
        
        db.collection("events").document(id.toString()).set(event)
    }

    fun loadEventData() {
        db.collection("events").orderBy("id").addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(TAG, "Failed to load news", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val documents = snapshot.documents
                documents.forEach {
                    val events = it.toObject(Event::class.java)
                    if (events != null) {
                        if (!eventList.contains(events)) {
                            eventList.add(events!!)
                        }
                    }
                }
            }
        }
        sortEventList()
    }

    private fun sortEventList() {
        eventList.sortByDescending { event ->
            event.id
        }
    }

    fun getAllEvents(): List<Event> {
        return eventList
    }

    fun deleteEvent(id: Int) {
        db.collection("events").document(id.toString()).delete()
    }

    fun updateEvent() {
        //TODO
    }

    fun getEventById(id: Int): Event? {
        for (event in eventList) {
            if (event.id == id) {
                return event
            }
        }
        return null
    }
}