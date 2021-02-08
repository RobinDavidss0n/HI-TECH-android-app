package se.ju.student.hitech

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG_FRAGMENT_SHOP = "TAG_FRAGMENT_SHOP"
        const val TAG_FRAGMENT_EVENTS = "TAG_FRAGMENT_EVENTS"
        const val TAG_FRAGMENT_NEWS = "TAG_FRAGMENT_NEWS"
        const val TAG_FRAGMENT_CONTACT = "TAG_FRAGMENT_CONTACT"
        const val TAG_FRAGMENT_ADMIN_LOGIN = "TAG_FRAGMENT_ADMIN_LOGIN"
        const val TAG_FRAGMENT_ABOUT = "TAG_FRAGMENT_ABOUT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_hitech_logo)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    /*  override fun onOptionsItemSelected(item: MenuItem): Boolean {
          when(item.itemId){
              R.id.nav_login -> changeToFragment(TAG_FRAGMENT_ADMIN_LOGIN)
              R.id.nav_about -> changeToFragment(TAG_FRAGMENT_ABOUT)
              R.id.nav_problem -> showReportProblemAlert()
          }
          return true
      }   */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_login -> {
                // Toast.makeText(applicationContext, "click on Log in", Toast.LENGTH_LONG).show()
                changeToFragment(TAG_FRAGMENT_ADMIN_LOGIN)
                return true
            }
            R.id.nav_about -> {
                // Toast.makeText(applicationContext, "click on About", Toast.LENGTH_LONG).show()
                changeToFragment(TAG_FRAGMENT_ABOUT)
                return true
            }
            R.id.nav_problem -> {
                //  Toast.makeText(applicationContext, "click on Problem", Toast.LENGTH_LONG).show()
                showReportProblemAlert()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showReportProblemAlert() {
        AlertDialog.Builder(this)
            .setTitle("Report issue")
            .setMessage("What is the problem?")
            .setPositiveButton(
                "Send"
            ) { dialog, whichButton ->
                // Send information in textbox
            }.setNegativeButton(
                "Go back"
            ) { dialog, whichButton ->
                // Do nothing
            }.show()
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

    /*

    val newsFragment = NewsFragment()
    val eventsFragment = EventsFragment()
    val hiShopFragment = ShopFragment()
    val contactFragment = ContactFragment()


    val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNav.setOnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.nav_news -> makeCurrentFragment(newsFragment)
            R.id.nav_events -> makeCurrentFragment(eventsFragment)
            R.id.nav_shop -> makeCurrentFragment(hiShopFragment)
            R.id.nav_contact -> makeCurrentFragment(contactFragment)
        }
        true
    }

    makeCurrentFragment(newsFragment)

    /*    if (savedInstanceState == null) {
            val fragment = NewsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)    */
}

private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container,fragment)
            commit()
        }

override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.options_menu, menu)
    return true
}

private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
    when (menuItem.itemId) {
        R.id.nav_events -> {
            val fragment = EventsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
            return@OnNavigationItemSelectedListener true
        }
        R.id.nav_shop -> {
            val fragment = ShopFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
            return@OnNavigationItemSelectedListener true
        }
        R.id.nav_news -> {
            val fragment = NewsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
            return@OnNavigationItemSelectedListener true
        }

      /*  R.id.nav_contact -> {
            val fragment = ContactActivity()
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName)
                    .commit()
            return@OnNavigationItemSelectedListener true
        }   */
    }
    false
}   */

//}
}