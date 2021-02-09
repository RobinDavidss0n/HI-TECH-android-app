package se.ju.student.hitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class CreateNewsPostFragment : Fragment() {

    companion object {
        const val TAG_FRAGMENT_NEWS = "TAG_FRAGMENT_NEWS"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_create_news, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            // change to view new post
            //   (context as MainActivity).changeToFragment()
        }

        view?.findViewById<Button>(R.id.button2)?.setOnClickListener {
            // go back to news fragment
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
        }
    }
}