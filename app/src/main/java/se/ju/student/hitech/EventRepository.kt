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
    var events = MutableLiveData<List<Event>>()

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

    fun loadEventData(
        events: MutableLiveData<List<Event>>,
        callback: (List<Event>, MutableLiveData<List<Event>>) -> Unit
    ) {

        db.collection("events").get().addOnSuccessListener { result ->

            eventList = result.toObjects(Event::class.java)
            sortEventList()
            callback(eventList, events)

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Error getting documents: ", it)
        }

    }

    fun updateEventList() {
        db.collection("events").get().addOnSuccessListener { result ->

            eventList = result.toObjects(Event::class.java)
            sortEventList()
            events.value = eventList

            EventsFragment.EventAdapter(eventList).notifyDataSetChanged()

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Error getting documents: ", it)
        }
    }

    private fun sortEventList() {
        eventList.sortByDescending { event ->
            event.id
        }
    }

    fun deleteEvent(id: Int) {
        db.collection("events").document(id.toString()).delete()
    }

    fun updateEvent() {
        //TODO
    }

    fun getEventById(id: Int): Event {
        return eventList[id]
    }


}