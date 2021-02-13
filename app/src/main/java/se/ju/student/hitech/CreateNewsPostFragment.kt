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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment

class CreateNewsPostFragment : Fragment() {

    companion object {
        const val TAG_FRAGMENT_NEWS = "TAG_FRAGMENT_NEWS"
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATIONS_ID = 101
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_create_news, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

     //   createNotificationChannel()

        view?.findViewById<CheckBox>(R.id.checkbox_notification)?.setOnClickListener {
            onCheckBoxClicked(it)
        }

        view?.findViewById<Button>(R.id.button)?.setOnClickListener {
            // send notification

            // change fragment to view new post
            //   (context as MainActivity).changeToFragment()


        }

        view?.findViewById<Button>(R.id.button2)?.setOnClickListener {
            // go back to news fragment
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_NEWS)
        }
    }

    fun onCheckBoxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            if (checked) {
                // create notification
                var builder = context?.let {
                    NotificationCompat.Builder(it, CHANNEL_ID)
                            .setContentTitle("Hej")
                            .setContentText("Hej")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                }
            }
        }
    }

 /*   private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }   */
}