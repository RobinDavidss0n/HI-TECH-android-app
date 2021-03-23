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
                    Log.d("Error fireStore", "Error loading news from fireStore")
                }
                "successful" -> {
                    latestId = list.last().id
                    val news = HashMap<String, Any>()
                    news["title"] = title
                    news["content"] = content
                    news["id"] = latestId + 1

                    db.collection("news").document(news["id"].toString()).set(news)
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
                    Log.w("Listen for news changes error ", error)
                    callback("internalError", mutableListOf(News()))
                } else {
                    val currentNewsList = mutableListOf<News>()
                    querySnapshot?.documents?.forEach { doc ->
                        val newsPost = doc.toObject(News::class.java)!!
                        currentNewsList.add(newsPost)
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
                    val newsPost = DocumentSnapshot.toObject(News::class.java)!!
                    currentNewsList.add(newsPost)
                }
                callback("successful", currentNewsList)
            }

        }.addOnFailureListener { error ->
            Log.w("Load all news data error", error)
            callback("internalError", mutableListOf(News()))
        }
    }

    fun deleteNews(id: Int): Task<Void> {
        return db.collection("news").document(id.toString()).delete()
    }

    fun updateNews(newTitle: String, newContent: String, id: Int): Task<Void> {
        val news = hashMapOf(
            "title" to newTitle,
            "content" to newContent,
            "id" to id
        )
        return db.collection("news").document(id.toString()).set(news)
    }

    fun getNewsById(id: Int, callback: (String, News) -> Unit) {
        db.collection("news").document(id.toString()).get().addOnSuccessListener {
            val news = it.toObject(News::class.java)
            if (news != null) {
                callback("successful", news)
            }
        }.addOnFailureListener {
            callback("internalError", News())
        }
    }
}