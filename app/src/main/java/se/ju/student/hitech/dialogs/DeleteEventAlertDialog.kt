package se.ju.student.hitech.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
import se.ju.student.hitech.events.EventRepository
import se.ju.student.hitech.events.EventRepository.Companion.eventRepository

class DeleteEventAlertDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var eventId: Int? = null

        if (arguments != null) {
            eventId = requireArguments().getInt("event_id")
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_event))
            .setMessage(getString(R.string.delete_event_are_you_sure))
            .setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                // delete event
                if (eventId != null) {
                    eventRepository.deleteEvent(eventId).addOnFailureListener {
                        MainActivity().makeToast(getString(R.string.error_delete_event))
                    }
                }
            }.setNegativeButton(
                getString(R.string.no)
            ) { _, _ ->
                // Do not delete event
            }.create()
    }

    companion object {
        const val TAG_DELETE_EVENT_DIALOG = "DeleteEventDialog"
    }
}