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
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class CreateNewsPostFragment : Fragment() {

    private var checked = false

    companion object {
        const val TAG_FRAGMENT_NEWS = "TAG_FRAGMENT_NEWS"
    }

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

        view?.findViewById<CheckBox>(R.id.checkbox_notification)
            ?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checked = true
                }
            }

        view?.findViewById<Button>(R.id.btn_create_news_create_post)?.setOnClickListener {
            if (checked) {
                // send notification
                    /*
                if (title != "" && content != "") {
                 //   (context as MainActivity).createNotification(title, content)
                    checked = false
                }
                     */

            }
        }

        view?.findViewById<Button>(R.id.btn_create_news_create_post)?.setOnClickListener() {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val novelty = HashMap<String, Any>()

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
    }

}