package se.ju.student.hitech.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
import se.ju.student.hitech.news.NewsRepository

class DeleteNewsAlertDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var newsId: Int? = null

        if (arguments != null) {
            newsId = requireArguments().getInt("news_id")
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_news_post))
            .setMessage(getString(R.string.delete_post_are_you_sure))
            .setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                // delete news post
                if (newsId != null) {
                    NewsRepository.newsRepository.deleteNews(newsId).addOnFailureListener {
                        MainActivity().makeToast(getString(R.string.error_delete_news_post))
                    }
                }
            }.setNegativeButton(
                getString(R.string.no)
            ) { _, _ ->
                // Do not delete post
            }.create()
    }

    companion object {
        const val TAG_DELETE_NEWS_DIALOG = "DeleteNewsDialog"
    }
}