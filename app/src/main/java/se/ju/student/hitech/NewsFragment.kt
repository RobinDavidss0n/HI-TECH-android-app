package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.okhttp.internal.Internal.instance
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_CREATE_NEWS_POST
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewsFragment : Fragment() {
      //  private val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_news, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val rv_recyclerView = view?.findViewById<RecyclerView>(R.id.rv_recyclerView)
        //https://www.youtube.com/watch?v=ai9rSGcDhyQ&t=259s


        rv_recyclerView?.layoutManager = LinearLayoutManager(context)
        rv_recyclerView?.adapter = newsRepository.loadNewsData()

        // progressBar?.visibility = View.GONE
        //

        view?.findViewById<Button>(R.id.btn_news_newPost)?.setOnClickListener() {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEWS_POST)
        }

    }

}



