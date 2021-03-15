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
    private var newsList = mutableListOf<Novelty>()

    companion object{
        val newsRepository = NewsRepository()
    }

    fun addNovelty(title: String, content: String): Task<Void> {

        val novelty = HashMap<String, Any>()
        sortNewsList()
        novelty["title"] = title
        novelty["content"] = content
        novelty["id"] = when {
            newsList.count() == 0 -> 1
            else -> newsList.first().id + 1
        }

        return db.collection("news").document(novelty["id"].toString()).set(novelty)
    }

    fun getAllNews(): List<Novelty> {
        return newsList
    }

    fun loadChangesInNewsData(): ListenerRegistration {
        return db.collection("news").orderBy("id").addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(TAG, "Failed to load news", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                for (dc in snapshot.documentChanges) {
                    val novelty = dc.document.toObject(Novelty::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> added(novelty)
                        DocumentChange.Type.MODIFIED -> modified(novelty)
                        DocumentChange.Type.REMOVED -> removed(novelty)
                    }
                }
            }
        }
    }

    private fun added(novelty: Novelty) {
        if (!newsList.contains(novelty)) {
            newsList.add(novelty)
        }
    }

    private fun modified(novelty: Novelty) {
        val item = newsList?.find { it.id == novelty.id }
        val index = newsList.indexOf(item)
        Log.d("index", index.toString())
        newsList[index] = novelty
    }

    private fun removed(novelty: Novelty) {
        if (newsList.contains(novelty)) {
            newsList.remove(novelty)
        }
    }

    fun deleteNovelty(id: Int): Task<Void> {
        return db.collection("news").document(id.toString()).delete()

    }

    private fun sortNewsList() {
        newsList.sortByDescending { novelty ->
            novelty.id
        }
    }

    fun updateNovelty(newTitle : String, newContent : String, id : Int) {
        val novelty = hashMapOf(
            "title" to newTitle,
            "content" to newContent,
            "id" to id
        )
        db.collection("news").document(id.toString()).set(novelty)
    }

    fun getNoveltyById(id: Int): Novelty? {

        for (novelty in newsList) {
            if (novelty.id == id) {
                return novelty
            }
        }
        return null
    }
}