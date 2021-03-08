package se.ju.student.hitech

import android.content.ContentValues
import android.util.Log
import android.widget.Adapter
import android.widget.BaseExpandableListAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.List as List

var newsRepository = NewsRepository()


class NewsRepository{

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var newsList = mutableListOf<Novelty>()
    var news = MutableLiveData<List<Novelty>>()

    fun addNovelty(title: String, content: String){

        val novelty = HashMap<String, Any>()
        sortNewsList()
        novelty["title"] = title
        novelty["content"] = content
        novelty["id"] = when {
            newsList.count() == 0 -> 1
            else -> newsList.last().id+1
        }
        db.collection("news")
            .add(novelty)
            .addOnSuccessListener { documentReference ->
                updateNewsList()
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }

    }


    fun loadNewsData(news:MutableLiveData<List<Novelty>>,callback:(List<Novelty>,MutableLiveData<List<Novelty>>)->Unit){

        db.collection("news").get().addOnSuccessListener { result ->

            newsList = result.toObjects(Novelty::class.java)
            sortNewsList()
            callback(newsList,news)

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Error getting documents: ", it)
        }

    }

    fun updateNewsList(){
        db.collection("news").get().addOnSuccessListener { result ->

            newsList = result.toObjects(Novelty::class.java)
            sortNewsList()
            news.value = newsList

            NewsFragment.NewsAdapter(newsList).notifyDataSetChanged()

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Error getting documents: ", it)
        }
    }

    fun sortNewsList(){
        newsList.sortBy{ novelty ->
            novelty.id
        }
    }

    fun getNoveltyById(id: Int):Novelty{
        return newsList[id]
    }


}


