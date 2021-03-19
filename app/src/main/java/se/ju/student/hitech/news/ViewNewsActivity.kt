package se.ju.student.hitech.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import se.ju.student.hitech.R

class ViewNewsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NEWS_ID = "NEWS_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HITECH)
        setContentView(R.layout.activity_view_news)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_hitech_logo_20)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val newsId = intent.getIntExtra(EXTRA_NEWS_ID, 0)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_layout, ViewNewsFragment.newInstance(newsId))
                .commit()
        }
    }
}