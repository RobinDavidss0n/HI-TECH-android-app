package se.ju.student.hitech.news

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_NEWS
import se.ju.student.hitech.MainActivity.Companion.TOPIC_NEWS
import se.ju.student.hitech.R
import se.ju.student.hitech.news.NewsRepository.Companion.newsRepository

class UpdateNoveltyFragment : Fragment() {

    private var checked = false
    private var noveltyId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_novelty, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val notificationContent =
            view?.findViewById<EditText>(R.id.editTextUpdatePostNotificationContent)
        val title = view?.findViewById<EditText>(R.id.editTextUpdatePostTitle)
        val content = view?.findViewById<EditText>(R.id.editTextUpdatePostContent)
        val updateNoveltyButton = view?.findViewById<Button>(R.id.btn_update_post)

        title?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                updateNoveltyButton?.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNoveltyButton?.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                updateNoveltyButton?.isEnabled = title.length() > 0 && content?.length()!! > 0
            }
        })

        content?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                updateNoveltyButton?.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNoveltyButton?.isEnabled = count > 0
            }

            override fun afterTextChanged(s: Editable?) {
                updateNoveltyButton?.isEnabled = content.length() > 0 && title?.length()!! > 0
            }

        })

        view?.findViewById<CheckBox>(R.id.checkbox_notification)
            ?.setOnCheckedChangeListener { _, isChecked ->
                checked = isChecked
            }

        view?.findViewById<CheckBox>(R.id.checkbox_notification)?.setOnClickListener {
            checked = true

            // set to false?
        }

        updateNoveltyButton?.setOnClickListener {
            newsRepository.updateNovelty(title!!.text.toString(), content!!.text.toString(), noveltyId)

            if (checked) {
                if (createNotification(title.text.toString(), notificationContent?.text.toString())) {
                    (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                } else {
                    (context as MainActivity).makeToast("Failed to create notification")
                }
            } else {
                (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
            }
        }

        view?.findViewById<Button>(R.id.btn_update_news_back)?.setOnClickListener {
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

    fun clickedNovelty(id : Int) {
        noveltyId = id

        val title = view?.findViewById<EditText>(R.id.editTextUpdatePostTitle)
        val content = view?.findViewById<EditText>(R.id.editTextUpdatePostContent)
        val novelty = newsRepository.getNoveltyById(noveltyId)

        title?.setText(novelty?.title)
        content?.setText(novelty?.content)
    }
}
