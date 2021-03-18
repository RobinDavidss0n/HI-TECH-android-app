package se.ju.student.hitech.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_NEWS
import se.ju.student.hitech.MainActivity.Companion.TOPIC_NEWS
import se.ju.student.hitech.R
import se.ju.student.hitech.news.NewsRepository.Companion.newsRepository

class UpdateNoveltyFragment : Fragment() {

    private var checked = false
    private var noveltyId = 0

    companion object {
        val updateNoveltyFragment = UpdateNoveltyFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_novelty, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationContent =
            view.findViewById<TextInputEditText>(R.id.editTextUpdatePostNotificationContent)
        val title = view.findViewById<TextInputEditText>(R.id.editTextUpdatePostTitle)
        val content = view.findViewById<TextInputEditText>(R.id.editTextUpdatePostContent)
        val updateNoveltyButton = view.findViewById<Button>(R.id.btn_update_post)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = GONE

        view.findViewById<CheckBox>(R.id.checkbox_notification)?.setOnClickListener {
            onCheckBoxClicked(it)
        }

        updateNoveltyButton?.setOnClickListener {

            if (checked) {
                if (verifyPostNotificationUserInputs(
                        title.text.toString(),
                        content.text.toString(),
                        notificationContent.text.toString()
                    )
                ) {
                    progressBar.visibility = VISIBLE
                    newsRepository.updateNovelty(
                        title.text.toString(),
                        content.text.toString(),
                        noveltyId
                    ).addOnSuccessListener {
                        progressBar?.visibility = GONE
                        createNotification(
                            title.text.toString(),
                            notificationContent.text.toString()
                        )
                        (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                        title.setText("")
                        content.setText("")
                        notificationContent.setText("")
                    }.addOnFailureListener {
                        progressBar?.visibility = GONE
                        (context as MainActivity).makeToast(getString(R.string.failed_update_post))
                    }
                } else {
                    progressBar.visibility = GONE
                }
            } else {
                if (verifyPostUserInputs(title.text.toString(), content.text.toString())) {
                    progressBar.visibility = VISIBLE
                    newsRepository.updateNovelty(
                        title.text.toString(),
                        content.text.toString(),
                        noveltyId
                    ).addOnSuccessListener {
                        progressBar?.visibility = GONE
                        (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
                        title.setText("")
                        content.setText("")
                        notificationContent.setText("")
                    }.addOnFailureListener {
                        progressBar?.visibility = GONE
                        (context as MainActivity).makeToast(getString(R.string.failed_update_post))
                    }
                } else {
                    progressBar.visibility = GONE
                }
            }
        }

        view.findViewById<Button>(R.id.btn_update_news_back)?.setOnClickListener {
            progressBar?.visibility = GONE
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
        }
    }

    private fun createNotification(title: String, content: String) {
        (context as MainActivity).createNotification(title, content, TOPIC_NEWS)
        checked = false
    }

    private fun verifyPostNotificationUserInputs(
        title: String,
        content: String,
        notificationContent: String
    ): Boolean {
        val notificationContentInputLayout =
            view?.findViewById<TextInputLayout>(R.id.textInputLayout_updatePostNotificationContent)
        val titleInputLayout =
            view?.findViewById<TextInputLayout>(R.id.textInputLayout_updatePostTitle)
        val contentInputLayout =
            view?.findViewById<TextInputLayout>(R.id.textInputLayout_updatePostContent)

        notificationContentInputLayout?.error = ""
        titleInputLayout?.error = ""
        contentInputLayout?.error = ""

        if (title.isEmpty()) {
            titleInputLayout?.error = getString(R.string.empty_title)
            return false
        }

        if (content.isEmpty()) {
            contentInputLayout?.error = getString(R.string.empty_content)
            return false
        }

        if (notificationContent.isEmpty()) {
            notificationContentInputLayout?.error =
                getString(R.string.empty_notification_description)
            return false
        }

        return true
    }

    private fun verifyPostUserInputs(title: String, content: String): Boolean {
        val notificationContentInputLayout =
            view?.findViewById<TextInputLayout>(R.id.textInputLayout_updatePostNotificationContent)
        val titleInputLayout =
            view?.findViewById<TextInputLayout>(R.id.textInputLayout_updatePostTitle)
        val contentInputLayout =
            view?.findViewById<TextInputLayout>(R.id.textInputLayout_updatePostContent)

        titleInputLayout?.error = ""
        contentInputLayout?.error = ""
        notificationContentInputLayout?.error = ""

        if (title.isEmpty()) {
            titleInputLayout?.error = getString(R.string.empty_title)
            return false
        }

        if (content.isEmpty()) {
            contentInputLayout?.error = getString(R.string.empty_content)
            return false
        }

        return true
    }

    fun onCheckBoxClicked(view: View) {
        if (view is CheckBox) {
            checked = view.isChecked
        }
    }

    fun clickedNovelty(id: Int) {
        noveltyId = id

        val title = view?.findViewById<TextInputEditText>(R.id.editTextUpdatePostTitle)
        val content = view?.findViewById<TextInputEditText>(R.id.editTextUpdatePostContent)
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
                    (context as MainActivity).makeToast(getString(R.string.error_loading_novelty))
                    title?.setText("")
                    content?.setText("")
                    Log.d("Error fireStore", "Error loading novelty from fireStore")
                    progressBar?.visibility = GONE
                }
            }
        }
    }
}