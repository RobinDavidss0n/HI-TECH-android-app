package se.ju.student.hitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class NewsFragment : Fragment() {

    companion object {
        const val TAG_FRAGMENT_CREATE_NEWS_POST = "TAG_FRAGMENT_CREATE_NEWS_POST"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_news, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.findViewById<Button>(R.id.btn_news_newPost)?.setOnClickListener() {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEWS_POST)
        }
    }
}