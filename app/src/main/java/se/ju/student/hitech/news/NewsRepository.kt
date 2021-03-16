package se.ju.student.hitech.news

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import se.ju.student.hitech.news.Novelty
import se.ju.student.hitech.shop.ShopFragment
import kotlin.collections.List as List

class NewsRepository {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var latestId: Int = 0

    companion object {
        val newsRepository = NewsRepository()
    }

    fun addNovelty(title: String, content: String): Task<Void> {

        val novelty = HashMap<String, Any>()
        novelty["title"] = title
        novelty["content"] = content
        novelty["id"] = when {
            latestId == 0 -> 1
            else -> latestId + 1
        }

        latestId += 1
        return db.collection("news").document(novelty["id"].toString()).set(novelty)
    }

    // SORTERA och hämta högsta

    fun listenForNewsChanges(
        callback: (String, MutableList<Novelty>) -> Unit
    ) {
        db.collection("news").orderBy("id")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mutableListOf(Novelty()))
                } else {
                    val currentNewsList = mutableListOf<Novelty>()
                    querySnapshot?.documents?.forEach { doc ->
                        val novelty = doc.toObject(Novelty::class.java)!!
                        currentNewsList.add(novelty)
                    }
                    callback("successful", currentNewsList)
                }
            }
    }

    fun loadAllNewsData(
        callback: (String, MutableList<Novelty>) -> Unit
    ) {
        db.collection("news").orderBy("id").get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                callback("notFound", mutableListOf(Novelty()))
            } else {
                snapshot.documents.forEach { DocumentSnapshot ->
                    val currentNewsList = mutableListOf<Novelty>()
                    val novelty = DocumentSnapshot.toObject(Novelty::class.java)!!
                    currentNewsList.add(novelty)
                    callback("successful", currentNewsList)
                }
            }

        }.addOnFailureListener { error ->
            Log.w("Get user info database error", error)
            callback("internalError", mutableListOf(Novelty()))
        }
    }

    fun deleteNovelty(id: Int) {
        db.collection("news").document(id.toString()).delete()
    }

    fun updateNovelty(newTitle: String, newContent: String, id: Int) {
        val novelty = hashMapOf(
            "title" to newTitle,
            "content" to newContent,
            "id" to id
        )
        db.collection("news").document(id.toString()).set(novelty)
    }

    fun getNoveltyById(id: Int, callback: (String, Novelty) -> Unit) {
        db.collection("news").document(id.toString()).get().addOnSuccessListener {
            val novelty = it.toObject(Novelty::class.java)
            if (novelty != null) {
                callback("successful", novelty)
            }
        }.addOnFailureListener {
            callback("internalError", Novelty())
        }
    }
}