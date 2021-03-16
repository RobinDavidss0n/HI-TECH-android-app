package se.ju.student.hitech.news

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_NEWS
import se.ju.student.hitech.MainActivity.Companion.TOPIC_NEWS
import se.ju.student.hitech.R
import se.ju.student.hitech.news.NewsRepository.Companion.newsRepository

class CreateNoveltyFragment : Fragment() {

    private var checked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_novelty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationContent =
            view.findViewById<EditText>(R.id.editTextNewPostNotificationContent)
        val title = view.findViewById<EditText>(R.id.editTextNewPostTitle)
        val content = view.findViewById<EditText>(R.id.editTextNewPostContent)
        val createNoveltyButton = view.findViewById<Button>(R.id.btn_create_news_create_post)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        progressBar?.visibility = GONE

        title?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                createNoveltyButton?.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createNoveltyButton?.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                createNoveltyButton?.isEnabled = title.length() > 0 && content?.length()!! > 0
            }
        })

        content?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                createNoveltyButton?.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createNoveltyButton?.isEnabled = count > 0
            }

            override fun afterTextChanged(s: Editable?) {
                createNoveltyButton?.isEnabled = content.length() > 0 && title?.length()!! > 0
            }

        })

        view.findViewById<CheckBox>(R.id.checkbox_notification)?.setOnClickListener {
            onCheckBoxClicked(it)
        }

        createNoveltyButton?.setOnClickListener {
            progressBar?.visibility = VISIBLE

            newsRepository.addNovelty(title!!.text.toString(), content!!.text.toString()){ result ->
                when(result){
                    "successful" -> {
                        if (checked) {
                            if (createNotification(title.text.toString(), notificationContent?.text.toString())) {
                                (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                                progressBar?.visibility = GONE
                            } else {
                                progressBar?.visibility = GONE
                                (context as MainActivity).makeToast("Failed to create notification")
                            }
                        } else{
                            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                            progressBar?.visibility = GONE
                        }
                    }
                    "internalError" -> {
                        progressBar?.visibility = GONE
                        (context as MainActivity).makeToast("Failed to create post")
                    }
                }
            }
        }

        view.findViewById<Button>(R.id.btn_create_news_back)?.setOnClickListener {
            progressBar?.visibility = GONE
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
        }
    }

    private fun createNotification(title: String, content: String): Boolean {
        return if (title != "" && content != "") {
            (context as MainActivity).createNotification(
                title,
                content,
                TOPIC_NEWS
            )
            checked = false
            true
        } else {
            (context as MainActivity).makeToast("Notification fields can't be empty")
            false
        }
    }

    fun onCheckBoxClicked(view: View) {
        if (view is CheckBox) {
            checked = view.isChecked
        }
    }
}
