package se.ju.student.hitech.news

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class NewsRepository {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var latestId: Int = 0

    companion object {
        val newsRepository = NewsRepository()
    }

    fun addNews(title: String, content: String, callback: (String) -> Unit) {

        loadAllNewsData { result, list ->
            when (result) {
                "notFound" -> {
                    Log.d("Error fireStore", "Error loading novelty from fireStore")
                }
                "successful" -> {
                    latestId = list.last().id
                    val novelty = HashMap<String, Any>()
                    novelty["title"] = title
                    novelty["content"] = content
                    novelty["id"] = latestId + 1

                    db.collection("news").document(novelty["id"].toString()).set(novelty)
                        .addOnCompleteListener {
                            callback("successful")
                        }.addOnFailureListener {
                            callback("internalError")
                        }
                }
            }
        }
    }

    fun listenForNewsChanges(
        callback: (String, MutableList<News>) -> Unit
    ) {
        db.collection("news").orderBy("id")
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.w("Messages listener error ", error)
                    callback("internalError", mutableListOf(News()))
                } else {
                    val currentNewsList = mutableListOf<News>()
                    querySnapshot?.documents?.forEach { doc ->
                        val novelty = doc.toObject(News::class.java)!!
                        currentNewsList.add(novelty)
                    }
                    callback("successful", currentNewsList)
                }
            }
    }

    private fun loadAllNewsData(
        callback: (String, MutableList<News>) -> Unit
    ) {
        db.collection("news").orderBy("id").get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                callback("notFound", mutableListOf(News()))
            } else {
                val currentNewsList = mutableListOf<News>()
                snapshot.documents.forEach { DocumentSnapshot ->
                    val novelty = DocumentSnapshot.toObject(News::class.java)!!
                    currentNewsList.add(novelty)
                }
                callback("successful", currentNewsList)
            }

        }.addOnFailureListener { error ->
            Log.w("Get user info database error", error)
            callback("internalError", mutableListOf(News()))
        }
    }

    fun deleteNews(id: Int): Task<Void> {
        return db.collection("news").document(id.toString()).delete()
    }

    fun updateNews(newTitle: String, newContent: String, id: Int): Task<Void> {
        val novelty = hashMapOf(
            "title" to newTitle,
            "content" to newContent,
            "id" to id
        )
        return db.collection("news").document(id.toString()).set(novelty)
    }

    fun getNewsById(id: Int, callback: (String, News) -> Unit) {
        db.collection("news").document(id.toString()).get().addOnSuccessListener {
            val novelty = it.toObject(News::class.java)
            if (novelty != null) {
                Log.d("success loading novelty", it.toString())
                callback("successful", novelty)
            }
        }.addOnFailureListener {
            Log.d("error get novelty by id", it.toString())
            callback("internalError", News())
        }
    }
}