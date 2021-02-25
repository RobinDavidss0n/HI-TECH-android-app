package se.ju.student.hitech

import android.content.ContentValues.TAG
import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_create_news, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val title = view?.findViewById<EditText>(R.id.editTextNewPostTitle)?.text
        val content = view?.findViewById<EditText>(R.id.editTextNewPostContent)?.text
        val notificationContent =
            view?.findViewById<EditText>(R.id.editTextNewPostNotificationContent)?.text

        view?.findViewById<CheckBox>(R.id.checkbox_notification)
            ?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checked = true
                }
            }

        view?.findViewById<Button>(R.id.btn_create_news_back)?.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_NEWS)
        }

        view?.findViewById<Button>(R.id.btn_create_news_create_post)?.setOnClickListener {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val novelty = HashMap<String, Any>()

            if (title.toString() != "" && content.toString() != "") {
                novelty["title"] = title.toString()
                novelty["content"] = content.toString()

                db.collection("news")
                    .add(novelty)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_NEWS)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
            // else toast error message?

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
                // else toast error message?
            }   */
        }
    }
}