package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.internal.Internal.instance
import java.util.*

class NewsFragment : Fragment() {

    companion object {
        const val TAG_FRAGMENT_CREATE_NEWS_POST = "TAG_FRAGMENT_CREATE_NEWS_POST"

    }

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


        view?.findViewById<Button>(R.id.btn_news_newPost)?.setOnClickListener() {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEWS_POST)
        }

    }
}