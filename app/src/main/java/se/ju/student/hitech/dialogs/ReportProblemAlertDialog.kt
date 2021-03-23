package se.ju.student.hitech.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R

class ReportProblemAlertDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_report_problem, null)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.problem)
            .setView(dialogView)
            .setPositiveButton(
                R.string.send
            ) { dialog, whichButton ->
                // Send email from users input
                val mail = dialogView.findViewById<EditText>(R.id.edittext_problem).text.toString()
                (context as MainActivity).sendEmail(mail)
            }.setNegativeButton(
                R.string.cancel
            ) { dialog, whichButton ->
                // Do nothing
            }.create()
    }

    companion object {
        const val TAG_REPORT_PROBLEM_DIALOG = "ReportProblemAlertDialog"
    }
}