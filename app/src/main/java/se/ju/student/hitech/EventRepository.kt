package se.ju.student.hitech

import android.content.ContentValues
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

    fun addEvent(title: String, date: String, time: String, location: String, information: String) {
        val event = HashMap<String, Any>()

        event["title"] = title
        event["date"] = date
        event["time"] = time
        event["location"] = location
        event["information"] = information
        event["id"] = when {
            eventList.count() == 0 -> 1
            else -> eventList.last().id + 1
        }
        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                EventsFragment.EventAdapter(eventList).notifyItemInserted(eventList.size)
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot written with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }
}