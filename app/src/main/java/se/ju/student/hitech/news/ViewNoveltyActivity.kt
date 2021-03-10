package se.ju.student.hitech.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import se.ju.student.hitech.R
import se.ju.student.hitech.news.ViewNoveltyFragment

class ViewNoveltyActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_NOVELTY_ID= "NOVELTY_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HITECH)
        setContentView(R.layout.activity_view_novelty)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_hitech_logo_20)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val noveltyId = intent.getIntExtra(EXTRA_NOVELTY_ID,0)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_layout, ViewNoveltyFragment.newInstance(noveltyId))
                .commit()
        }
    }
}