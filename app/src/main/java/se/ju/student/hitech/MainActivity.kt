package se.ju.student.hitech

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.ju.student.hitech.chat.ActiveChatsFragmentUser
import se.ju.student.hitech.chat.ChatRepository
import se.ju.student.hitech.chat.ContactCaseFragment
import se.ju.student.hitech.events.CreateNewEventFragment
import se.ju.student.hitech.events.EventsFragment
import se.ju.student.hitech.events.UpdateEventFragment.Companion.updateEventFragment
import se.ju.student.hitech.news.CreateNewsFragment
import se.ju.student.hitech.news.NewsFragment
import se.ju.student.hitech.news.UpdateNewsFragment.Companion.updateNewsFragment
import se.ju.student.hitech.notifications.NotificationData
import se.ju.student.hitech.notifications.PushNotification
import se.ju.student.hitech.notifications.RetrofitInstance
import se.ju.student.hitech.shop.ShopFragment
import se.ju.student.hitech.user.UserLoginFragment
import se.ju.student.hitech.user.RegisterUserFragment
import se.ju.student.hitech.user.UserPageFragment
import se.ju.student.hitech.user.UserRepository.Companion.userRepository
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import se.ju.student.hitech.chat.ContactFragment
import se.ju.student.hitech.dialogs.ReportProblemAlertDialog
import se.ju.student.hitech.dialogs.ReportProblemAlertDialog.Companion.TAG_REPORT_PROBLEM_DIALOG
import se.ju.student.hitech.user.UserRepository
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var currentFragmentShowing = ""

    companion object {
        const val TAG_FRAGMENT_CREATE_NEW_EVENT = "TAG_FRAGMENT_NEW_EVENT"
        const val TAG_FRAGMENT_SHOP = "TAG_FRAGMENT_SHOP"
        const val TAG_FRAGMENT_EVENTS = "TAG_FRAGMENT_EVENTS"
        const val TAG_FRAGMENT_CONTACT_CASE = "TAG_FRAGMENT_CONTACT_CASE"
        const val TAG_FRAGMENT_CONTACT_USER_VIEW = "TAG_FRAGMENT_CONTACT_USER_VIEW"
        const val TAG_FRAGMENT_NEWS = "TAG_FRAGMENT_NEWS"
        const val TAG_FRAGMENT_CONTACT = "TAG_FRAGMENT_CONTACT"
        const val TAG_FRAGMENT_ADMIN_LOGIN = "TAG_FRAGMENT_ADMIN_LOGIN"
        const val TAG_FRAGMENT_ABOUT = "TAG_FRAGMENT_ABOUT"
        const val TAG_MAIN_ACTIVITY = "MainActivity"
        const val TAG_ADMIN_EMAIL = "it.hitech@js.ju.se"
        const val TOPIC_NEWS = "/topics/news"
        const val TAG_FRAGMENT_CREATE_NEWS = "TAG_FRAGMENT_CREATE_NEWS"
        const val TAG_REGISTER_USER = "TAG_FRAGMENT_REGISTER_USER"
        const val TAG_USER_PAGE = "TAG_FRAGMENT_USER_PAGE"
        const val TAG_FRAGMENT_UPDATE_EVENT = "TAG_FRAGMENT_UPDATE_EVENT"
        const val TAG_FRAGMENT_UPDATE_NEWS = "TAG_FRAGMENT_UPDATE_NEWS"
        const val TAG_CURRENT_FRAGMENT = "TAG_CURRENT_FRAGMENT"
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
                .add(R.id.fragment_container, UserLoginFragment(), TAG_FRAGMENT_ADMIN_LOGIN)
                .add(R.id.fragment_container, AboutFragment(), TAG_FRAGMENT_ABOUT)
                .add(R.id.fragment_container, EventsFragment(), TAG_FRAGMENT_EVENTS)
                .add(R.id.fragment_container, ShopFragment(), TAG_FRAGMENT_SHOP)
                .add(R.id.fragment_container, RegisterUserFragment(), TAG_REGISTER_USER)
                .add(R.id.fragment_container, ContactCaseFragment(), TAG_FRAGMENT_CONTACT_CASE)
                .add(R.id.fragment_container, ContactFragment(), TAG_FRAGMENT_CONTACT)
                .add(
                    R.id.fragment_container,
                    ActiveChatsFragmentUser(),
                    TAG_FRAGMENT_CONTACT_USER_VIEW
                )
                .add(R.id.fragment_container, UserPageFragment(), TAG_USER_PAGE)
                .add(R.id.fragment_container, CreateNewsFragment(), TAG_FRAGMENT_CREATE_NEWS)
                .add(
                    R.id.fragment_container,
                    CreateNewEventFragment(),
                    TAG_FRAGMENT_CREATE_NEW_EVENT
                )
                .add(R.id.fragment_container, updateEventFragment, TAG_FRAGMENT_UPDATE_EVENT)
                .add(R.id.fragment_container, updateNewsFragment, TAG_FRAGMENT_UPDATE_NEWS)
                .commitNow()
            changeToFragment(TAG_FRAGMENT_NEWS)
        }

        // subscribe all users to news notifications
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEWS)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_news -> changeToFragment(TAG_FRAGMENT_NEWS)
                R.id.nav_events -> changeToFragment(TAG_FRAGMENT_EVENTS)
                R.id.nav_shop -> changeToFragment(TAG_FRAGMENT_SHOP)
                R.id.nav_contact -> {
                    checkWhichContactFragmentToShow { fragment ->
                        changeToFragment(fragment)
                    }
                }
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TAG_CURRENT_FRAGMENT, currentFragmentShowing)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        currentFragmentShowing = savedInstanceState.getString(TAG_CURRENT_FRAGMENT).toString()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // keep menu items unchecked when restoring state
        when (currentFragmentShowing) {
            TAG_FRAGMENT_ABOUT -> bottomNav.uncheckAllItems()
            TAG_FRAGMENT_ADMIN_LOGIN -> bottomNav.uncheckAllItems()
            TAG_USER_PAGE -> bottomNav.uncheckAllItems()
        }
    }

    fun createNotification(title: String, message: String, topic: String) {
        PushNotification(
            NotificationData(title, message),
            topic
        ).also {
            sendNotification(it)
        }
    }

    private fun checkWhichContactFragmentToShow(callback: (String) -> Unit) {
        if (UserRepository().checkIfLoggedIn()) {
            changeToFragment(TAG_FRAGMENT_CONTACT_USER_VIEW)
        } else {

            val progressBar = findViewById<ProgressBar>(R.id.MainProgressBar)
            progressBar.visibility = VISIBLE
            ChatRepository.chatRepository.getFirebaseInstallationsID { result, localID ->
                when (result) {
                    "successful" -> {
                        ChatRepository().getChatIDWithLocalID(localID) { result2, chatID ->
                            when (result2) {
                                "successful" -> {
                                    ChatRepository().setCurrentChatID(chatID)
                                    reloadContactFragment()
                                    callback(TAG_FRAGMENT_CONTACT)
                                }

                                "notFound" -> {
                                    callback(TAG_FRAGMENT_CONTACT_CASE)
                                }
                                "internalError" -> makeToast("Something went wrong, check your internet connection and try again.")
                            }
                            progressBar.visibility = GONE

                        }
                    }
                    "internalError" -> {
                        progressBar.visibility = GONE
                        makeToast(getString(R.string.internalError))
                    }
                }
            }

        }
    }

    fun reloadContactFragment() {
        val contactFragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CONTACT)
        if (contactFragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.remove(contactFragment)
            fragmentTransaction.add(
                R.id.fragment_container,
                ContactFragment(),
                TAG_FRAGMENT_CONTACT
            )
            fragmentTransaction.commit()
        }
    }

    fun setClickedNewsId(id: Int) {
        updateNewsFragment.getClickedNews(id)
    }

    fun setClickedEventId(id: Int) {
        updateEventFragment.clickedEvent(id)
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)

                if (response.isSuccessful) {
                    Log.d(TAG_MAIN_ACTIVITY, "successful")
                } else {
                    Log.e(TAG_MAIN_ACTIVITY, response.errorBody().toString())
                }
            } catch (e: Exception) {
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
                if (userRepository.checkIfLoggedIn()) {
                    changeToFragment(TAG_USER_PAGE)
                } else {
                    changeToFragment(TAG_FRAGMENT_ADMIN_LOGIN)
                }
                return true
            }
            R.id.nav_about -> {
                bottomNav.uncheckAllItems()
                changeToFragment(TAG_FRAGMENT_ABOUT)
                return true
            }
            R.id.nav_problem -> {
                ReportProblemAlertDialog().show(supportFragmentManager, TAG_REPORT_PROBLEM_DIALOG)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun sendEmail(message: String?) {
        val subject = getString(R.string.report_bug_email_subject)

        // email intent to HI TECH IT Manager
        val emailIntent =
            Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", TAG_ADMIN_EMAIL, null))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message.toString())
        try {
            (Intent.createChooser(emailIntent, getString(R.string.choose_email_client)))
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun changeToFragment(fragment_tag: String) {
        currentFragmentShowing = fragment_tag
        with(supportFragmentManager.beginTransaction()) {

            for (fragment in supportFragmentManager.fragments) {
                hide(fragment)
            }

            show(supportFragmentManager.findFragmentByTag(fragment_tag)!!)
            commit()
        }
    }

    fun reloadFragment(fragment_tag: String) {
        val fragment = supportFragmentManager.findFragmentByTag(fragment_tag)
        if (fragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.detach(fragment)
            fragmentTransaction.attach(fragment)
            fragmentTransaction.commit()
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}