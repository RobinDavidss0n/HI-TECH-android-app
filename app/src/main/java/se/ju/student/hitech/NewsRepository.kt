package se.ju.student.hitech

import android.content.ContentValues
import android.util.Log
import android.widget.Adapter
import android.widget.BaseExpandableListAdapter
import com.google.firebase.firestore.FirebaseFirestore

var newsRepository = NewsRepository()


class NewsRepository{

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var newsList: List<Novelty> = ArrayList()
    private var newsListAdapter: NewsRecyclerAdapter = NewsRecyclerAdapter(newsList)

    fun addNovelty(title: String, content: String){

        val novelty = HashMap<String, Any>()

        novelty["title"] = title
        novelty["content"] = content
        novelty["post_type"] = NewsRecyclerAdapter.POST_TYPE_NO_IMAGE // no image post

        db.collection("news")
            .add(novelty)
            .addOnSuccessListener { documentReference ->
                novelty["id"]= documentReference.id
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }


    fun loadNewsData():NewsRecyclerAdapter {

        db.collection("news").get().addOnSuccessListener { result ->
            newsList = result.toObjects(Novelty::class.java)
            newsListAdapter.news = newsList
            newsListAdapter.notifyDataSetChanged()

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Error getting documents: ", it)
        }
        return newsListAdapter
    }

}