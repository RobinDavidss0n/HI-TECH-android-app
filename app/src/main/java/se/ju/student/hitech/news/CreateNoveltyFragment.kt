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

class CreateNoveltyFragment : Fragment() {

    private var checked = false
    lateinit var title: EditText
    lateinit var content: EditText
    lateinit var createNoveltyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_create_news, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title = view?.findViewById(R.id.editTextNewPostTitle)!!
        content = view?.findViewById(R.id.editTextNewPostContent)!!
        createNoveltyButton = view?.findViewById(R.id.btn_create_news_create_post)!!

        val notificationContent =
            view?.findViewById<EditText>(R.id.editTextNewPostNotificationContent)?.text
        val notificationTitle = view?.findViewById<EditText>(R.id.editTextNewPostTitle)?.text

        title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                createNoveltyButton.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createNoveltyButton.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                createNoveltyButton.isEnabled = title.length() > 0 && content.length() > 0
            }

        })
        content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                createNoveltyButton.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createNoveltyButton.isEnabled = count > 0
            }

            override fun afterTextChanged(s: Editable?) {
                createNoveltyButton.isEnabled = content.length() > 0 && title.length() > 0
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

        createNoveltyButton.setOnClickListener {
            newsRepository.addNovelty(title.text.toString(), content.text.toString())

            if (checked) {
                if(createNotification(notificationTitle.toString(), notificationContent.toString())){
                    (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                } else{
                    (context as MainActivity).makeToast("Failed to create notification")
                }
            } else{
                (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
            }
        }

        view?.findViewById<Button>(R.id.btn_create_news_back)?.setOnClickListener {
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
}
