package se.ju.student.hitech.news

import android.os.Bundle
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
        private const val ARG_NEWS_ID = "NEWS_ID"

        fun newInstance(newsId: Int) =
            ViewNewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_NEWS_ID, newsId)
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
            if (it.containsKey(ARG_NEWS_ID)) {
                val newsId = requireArguments().getInt(ARG_NEWS_ID)
                newsRepository.getNewsById(newsId) { result, news ->
                    when (result) {
                        "successful" -> {
                            title.text = news.title
                            content.text = news.content
                            progressBar?.visibility = GONE
                        }
                        "internalError" -> {
                            //notify user about error
                            (context as MainActivity).makeToast(getString(R.string.error_loading_news_post))
                            progressBar?.visibility = GONE
                        }
                    }
                }
            }
        }
    }
}