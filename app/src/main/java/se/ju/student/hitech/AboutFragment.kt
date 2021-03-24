package se.ju.student.hitech

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_about, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.findViewById<Button>(R.id.button_visit_website)?.setOnClickListener {
            openNewTabWindow("https://hitech.nu/", this)
        }

        view?.findViewById<ImageButton>(R.id.imagebutton_instagram)?.setOnClickListener {
            openNewTabWindow("https://www.instagram.com/hitech.jth/", this)
        }

        view?.findViewById<ImageButton>(R.id.imagebutton_facebook)?.setOnClickListener {
            openNewTabWindow("https://www.facebook.com/hitech/", this)
        }

        view?.findViewById<ImageButton>(R.id.imagebutton_linkedin)?.setOnClickListener {
            openNewTabWindow(
                "https://se.linkedin.com/company/hi-tech-at-j%C3%B6nk%C3%B6ping-university",
                this
            )
        }
    }

    private fun openNewTabWindow(urls: String, context: AboutFragment) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
}