package se.ju.student.hitech

import android.os.Bundle
import android.provider.Settings.Global.getInt
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.lang.Integer.parseInt

private const val ARG_NOVELTY_ID = "NOVELTY_ID"

class ViewNoveltyFragment : Fragment() {

    companion object {

        fun newInstance(noveltyId: Int) =
            ViewNoveltyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_NOVELTY_ID, noveltyId)
                }
            }
    }

    private var novelty: Novelty? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_NOVELTY_ID)){
                val noveltyId = requireArguments().getInt(ARG_NOVELTY_ID)
                novelty = newsRepository.getNoveltyById(noveltyId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_view_novelty, container, false)

        novelty?.let {
            rootView.findViewById<TextView>(R.id.newsTitleNoImage).text = it.title
            rootView.findViewById<TextView>(R.id.newsContentNoImage).text = it.content
        }

        return rootView
    }

}