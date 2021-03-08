package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

var eventRepository = EventRepository()

class EventRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var eventList = mutableListOf<Event>()

    fun loadEvents(
        events: MutableLiveData<List<Event>>,
        callback: (List<Event>, MutableLiveData<List<Event>>) -> Unit
    ) {

        db.collection("events").get().addOnSuccessListener { result ->
            eventList = result.toObjects(Event::class.java)
            callback(eventList, events)

        }.addOnFailureListener {
            Log.d(TAG, "Error getting documents: ", it)
        }

    }
}