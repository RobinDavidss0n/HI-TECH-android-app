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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationContent =
            view.findViewById<EditText>(R.id.editTextUpdatePostNotificationContent)
        val title = view.findViewById<EditText>(R.id.editTextUpdatePostTitle)
        val content = view.findViewById<EditText>(R.id.editTextUpdatePostContent)
        val updateNoveltyButton = view.findViewById<Button>(R.id.btn_update_post)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        title?.setText("")
        content?.setText("")

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

        view.findViewById<CheckBox>(R.id.checkbox_notification)?.setOnClickListener {
            onCheckBoxClicked(it)
        }

        updateNoveltyButton?.setOnClickListener {

            progressBar?.visibility = VISIBLE
            newsRepository.updateNovelty(
                title!!.text.toString(),
                content!!.text.toString(),
                noveltyId
            ).addOnSuccessListener {
                if (checked) {
                    if (createNotification(
                            title.text.toString(),
                            notificationContent?.text.toString()
                        )
                    ) {
                        (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                        progressBar?.visibility = GONE
                    } else {
                        progressBar?.visibility = GONE
                        (context as MainActivity).makeToast(getString(R.string.failed_create_notification))
                        (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                    }
                } else {
                    progressBar?.visibility = GONE
                    (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                }
            }.addOnFailureListener {
                progressBar?.visibility = GONE
                (context as MainActivity).makeToast(getString(R.string.failed_update_post))
            }
        }

        view.findViewById<Button>(R.id.btn_update_news_back)?.setOnClickListener {
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
            (context as MainActivity).makeToast(getString(R.string.empty_notification))
            false
        }
    }

    fun onCheckBoxClicked(view: View) {
        if (view is CheckBox) {
            checked = view.isChecked
        }
    }

    fun clickedNovelty(id: Int) {
        noveltyId = id

        val title = view?.findViewById<EditText>(R.id.editTextUpdatePostTitle)
        val content = view?.findViewById<EditText>(R.id.editTextUpdatePostContent)
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)

        progressBar?.visibility = VISIBLE
        newsRepository.getNoveltyById(noveltyId) { result, novelty ->
            when (result) {
                "successful" -> {
                    title?.setText(novelty.title)
                    content?.setText(novelty.content)
                    progressBar?.visibility = GONE
                }
                "internalError" -> {
                    //notify user about error
                    title?.setText("")
                    content?.setText("")
                    Log.d("Error fireStore", "Error loading novelty from fireStore")
                    progressBar?.visibility = GONE
                }
            }
        }
    }
}
