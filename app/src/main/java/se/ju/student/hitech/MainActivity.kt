package se.ju.student.hitech

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG_FRAGMENT_SHOP = "TAG_FRAGMENT_SHOP"
        const val TAG_FRAGMENT_EVENTS = "TAG_FRAGMENT_EVENTS"
        const val TAG_FRAGMENT_NEWS = "TAG_FRAGMENT_NEWS"
        const val TAG_FRAGMENT_CONTACT = "TAG_FRAGMENT_CONTACT"
        const val TAG_FRAGMENT_ADMIN_LOGIN = "TAG_FRAGMENT_ADMIN_LOGIN"
        const val TAG_FRAGMENT_ABOUT = "TAG_FRAGMENT_ABOUT"
        const val TAG_MAIN_ACTIVITY = "MainActivity"
        const val TAG_ADMIN_EMAIL = "it.hitech@js.ju.se"
        const val TOPIC_NEWS = "/topics/news"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HITECH)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_hitech_logo_20)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, NewsFragment(), TAG_FRAGMENT_NEWS)
                    .add(R.id.fragment_container, AdminLoginFragment(), TAG_FRAGMENT_ADMIN_LOGIN)
                    .add(R.id.fragment_container, AboutFragment(), TAG_FRAGMENT_ABOUT)
                    .add(R.id.fragment_container, EventsFragment(), TAG_FRAGMENT_EVENTS)
                    .add(R.id.fragment_container, ShopFragment(), TAG_FRAGMENT_SHOP)
                    .add(R.id.fragment_container, ContactFragment(), TAG_FRAGMENT_CONTACT)
                    .commitNow()
            changeToFragment(TAG_FRAGMENT_NEWS)
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEWS)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_news -> changeToFragment(TAG_FRAGMENT_NEWS)
                R.id.nav_events -> changeToFragment(TAG_FRAGMENT_EVENTS)
                R.id.nav_shop -> changeToFragment(TAG_FRAGMENT_SHOP)
                R.id.nav_contact -> changeToFragment(TAG_FRAGMENT_CONTACT)
            }
            true
        }
    }

    fun createNotification(title: String, message: String){
        PushNotification(
            NotificationData(title, message),
            TOPIC_NEWS
        ).also {
            sendNotification(it)
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)

            if(response.isSuccessful){
                Log.d(TAG_MAIN_ACTIVITY, "Response: ${Gson().toJson(response)}")
            } else{
                Log.e(TAG_MAIN_ACTIVITY, response.errorBody().toString())
            }
        } catch (e: Exception){
            Log.e(TAG_MAIN_ACTIVITY, e.toString())
        }
    }

    private fun BottomNavigationView.uncheckAllItems() {
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        return when (item.itemId) {
            R.id.nav_login -> {
                bottomNav.uncheckAllItems()
                changeToFragment(TAG_FRAGMENT_ADMIN_LOGIN)
                return true
            }
            R.id.nav_about -> {
                bottomNav.uncheckAllItems()
                changeToFragment(TAG_FRAGMENT_ABOUT)
                return true
            }
            R.id.nav_problem -> {
                showReportProblemAlert()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showReportProblemAlert() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report_problem, null)

        AlertDialog.Builder(this)
                .setTitle(R.string.problem)
                .setView(dialogView)
                .setPositiveButton(
                        R.string.send
                ) { dialog, whichButton ->
                    // Send email from users input
                    val mail = dialogView.findViewById<EditText>(R.id.edittext_problem).text
                    sendEmail(mail)
                }.setNegativeButton(
                        R.string.cancel
                ) { dialog, whichButton ->
                    // Do nothing
                }.show()
    }

    private fun sendEmail(message: Editable?) {
        val subject = "Report problem HI TECH Android application"

        // email intent to HI TECH IT Manager
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", TAG_ADMIN_EMAIL, null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message.toString())
        try {
            (Intent.createChooser(emailIntent, "Choose email client.."))
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }


    fun changeToFragment(fragment_tag: String) {

        with(supportFragmentManager.beginTransaction()) {

            for (fragment in supportFragmentManager.fragments) {
                hide(fragment)
            }

            show(supportFragmentManager.findFragmentByTag(fragment_tag)!!)

            commit()
        }
    }

}