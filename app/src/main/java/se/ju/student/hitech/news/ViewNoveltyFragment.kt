package se.ju.student.hitech.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import se.ju.student.hitech.R
import se.ju.student.hitech.news.NewsRepository.Companion.newsRepository
import se.ju.student.hitech.news.Novelty

class ViewNoveltyFragment : Fragment() {

    companion object {
        private const val ARG_NOVELTY_ID = "NOVELTY_ID"

        fun newInstance(noveltyId: Int) =
            ViewNoveltyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_NOVELTY_ID, noveltyId)
                }
            }
    }

    private var clickedNovelty: Novelty? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_NOVELTY_ID)) {
                val noveltyId = requireArguments().getInt(ARG_NOVELTY_ID)
                newsRepository.getNoveltyById(noveltyId) { result, novelty ->
                    when (result) {
                        "successful" -> {
                            clickedNovelty = novelty
                        }
                        "internalError" -> {
                            //notify user about error
                            Log.d("Error fireStore", "Error loading novelty from fireStore")
                        }
                    }
                }
            }
        }

    /*    clickedNovelty?.let {
            val title = findViewById<TextView>(R.id.newsTitleNoImage).text = it.title
            rootView.findViewById<TextView>(R.id.newsContentNoImage).text = it.content
        }   */

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_novelty, container, false)
    }
}