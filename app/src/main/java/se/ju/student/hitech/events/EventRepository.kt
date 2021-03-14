package se.ju.student.hitech.events

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import se.ju.student.hitech.events.Event

class EventRepository {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var eventList = mutableListOf<Event>()

    companion object {
        var eventRepository = EventRepository()
    }

    fun updateEvent(newTitle: String, newDate: String, newTime: String, newLocation: String, newInformation: String, id : Int) {

        sortEventList()

       /* val event = hashMapOf(
            "title" to title,
            "date" to date,
            "time" to time,
            "location" to location,
            "information" to information,
            "id" to id
        )

        db.collection("events").document(id.toString()).set(event)  */
    }

    fun addEvent(title: String, date: String, time: String, location: String, information: String) {

        sortEventList()

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

    fun loadChangesInEventsData() {
        db.collection("events").orderBy("id").addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(TAG, "Failed to load news", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                for (dc in snapshot!!.documentChanges) {
                    val event = dc.document.toObject(Event::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> added(event)
                        DocumentChange.Type.MODIFIED -> modified(event)
                        DocumentChange.Type.REMOVED -> removed(event)
                    }
                }
            }
        }
    }

    private fun added(event: Event) {
        if (!eventList.contains(event)) {
            eventList.add(event)
        }
    }

    private fun modified(event: Event) {
        // remove old
        // add new
    }

    private fun removed(event: Event) {
        if (eventList.contains(event)) {
            eventList.remove(event)
        }
    }

    private fun sortEventList() {
        eventList.sortByDescending { event ->
            event.id
        }
    }

    fun getAllEvents(): List<Event> {
        // sortEventList()
        return eventList
    }

    fun deleteEvent(id: Int) {
        db.collection("events").document(id.toString()).delete()
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