package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_NEWS


class CreateNewsPostFragment : Fragment() {

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
        val notificationContent =
            view?.findViewById<EditText>(R.id.editTextNewPostNotificationContent)?.text

        view?.findViewById<CheckBox>(R.id.checkbox_notification)
            ?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checked = true
                }
            }

        createNoveltyButton.setOnClickListener {
            newsRepository.addNovelty(title.text.toString(), content.text.toString())
            /*          if (checked) {
               // send notification
               if (title.toString() != "" && notificationContent.toString() != "") {
                   (context as MainActivity).createNotification(
                       title.toString(),
                       notificationContent.toString(),
                       TOPIC_NEWS
                   )
                   checked = false
               }
               // else toast error message - fields can't be empty
           }   */


            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
        }

        view?.findViewById<Button>(R.id.btn_create_news_back)?.setOnClickListener {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
        }


    }
}