package se.ju.student.hitech.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
import se.ju.student.hitech.news.NewsRepository.Companion.newsRepository

class ViewNewsFragment : Fragment() {

    companion object {
        private const val ARG_NOVELTY_ID = "NOVELTY_ID"

        fun newInstance(noveltyId: Int) =
            ViewNewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_NOVELTY_ID, noveltyId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.textview_view_news_title)
        val content = view.findViewById<TextView>(R.id.textview_news_content)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        progressBar?.visibility = VISIBLE

        arguments?.let {
            if (it.containsKey(ARG_NOVELTY_ID)) {
                val noveltyId = requireArguments().getInt(ARG_NOVELTY_ID)
                newsRepository.getNewsById(noveltyId) { result, novelty ->
                    when (result) {
                        "successful" -> {
                            title.text = novelty.title
                            content.text = novelty.content
                            progressBar?.visibility = GONE
                        }
                        "internalError" -> {
                            //notify user about error
                            (context as MainActivity).makeToast(getString(R.string.error_loading_news_post))
                            progressBar?.visibility = GONE
                            Log.d("Error fireStore", "Error loading news from fireStore")
                        }
                    }
                }
            }
        }
    }
}