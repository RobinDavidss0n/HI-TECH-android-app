package se.ju.student.hitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_NEWS
import se.ju.student.hitech.MainActivity.Companion.TOPIC_NEWS

class test : Fragment() {

    private var checked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.test, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val title = view?.findViewById<EditText>(R.id.editTextTextPersonName)?.text
        val content = view?.findViewById<EditText>(R.id.editTextTextPersonName2)?.text

        view?.findViewById<CheckBox>(R.id.checkbox_notification)
            ?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checked = true
                }
            }

        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            if (checked) {
                // send notification
                if (title.toString() != "" && content.toString() != "") {
                    (context as MainActivity).createNotification(
                        title.toString(),
                        content.toString(),
                        TOPIC_NEWS
                    )
                    checked = false
                }

                // view new post
                //  (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)

            }
        }

    }
}