package se.ju.student.hitech

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import android.widget.Adapter
import android.widget.BaseExpandableListAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.thread
import kotlin.collections.List as List

var newsRepository = NewsRepository()


class NewsRepository {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var newsList = mutableListOf<Novelty>()
    //  var news = MutableLiveData<List<Novelty>>()

    fun addNovelty(title: String, content: String) {

        val novelty = HashMap<String, Any>()
        sortNewsList()
        novelty["title"] = title
        novelty["content"] = content
        novelty["id"] = when {
            newsList.count() == 0 -> 1
            else -> newsList.first().id + 1
        }

        db.collection("news").document(novelty["id"].toString()).set(novelty).addOnSuccessListener {
            //  updateNewsList()
            loadNewsData()
        }
    }

/*
    fun loadNewsData(news:MutableLiveData<List<Novelty>>,callback:(List<Novelty>,MutableLiveData<List<Novelty>>)->Unit){

        db.collection("news").get().addOnSuccessListener { result ->
            // addSnapshotlistener

            newsList = result.toObjects(Novelty::class.java)
            sortNewsList()
            callback(newsList,news)

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Error getting documents: ", it)
        }
    }
    */

    fun getAllNews(): List<Novelty> {
        return newsList
    }

    fun loadNewsData() {
        db.collection("news").addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(TAG, "Failed to load news", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val documents = snapshot.documents
                documents.forEach {
                    val novelty = it.toObject(Novelty::class.java)
                    if (novelty != null) {
                        if(!newsList.contains(novelty)){
                            newsList.add(novelty!!)
                        }

                    }

                }
            }

        }

    }

    fun deleteNovelty(id: Int) {
        db.collection("news").document(id.toString()).delete()
        // updateNewsList()
    }

    /*
    fun loadNewsData(){
        db.collection("news").addSnapshotListener{result,e ->
            sortNewsList()

            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            newsList = result!!.toObjects(Novelty::class.java)
        }

    }
     */

    private fun sortNewsList() {
        newsList.sortByDescending { novelty ->
            novelty.id
        }
    }

    fun updateNovelty() {
        // TODO
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


