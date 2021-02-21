package se.ju.student.hitech

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment

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

        val title = view?.findViewById<EditText>(R.id.editTextTextPersonName)?.text.toString()
        val content = view?.findViewById<EditText>(R.id.editTextTextPersonName2)?.text.toString()

        view?.findViewById<CheckBox>(R.id.checkbox_notification)
            ?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checked = true
                }
            }

        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            if (checked) {
                // send notification
                if (title != "" && content != "") {
                 //   (context as MainActivity).createNotification(title, content)
                    checked = false
                }

            }
        }


    }

}