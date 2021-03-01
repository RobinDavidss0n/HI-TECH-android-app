package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ShopRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun loadNewsData() {
        db.collection("news").get().addOnSuccessListener { result ->
        /*    newsList = result.toObjects(Novelty::class.java)
            newsListAdapter.news = newsList
            newsListAdapter.notifyDataSetChanged()  */

        }.addOnFailureListener {
            Log.d(TAG, "Error getting images: ", it)
        }
    }

}