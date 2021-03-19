package se.ju.student.hitech.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_EVENTS
import se.ju.student.hitech.R
import se.ju.student.hitech.databinding.FragmentUpdateEventBinding
import se.ju.student.hitech.events.EventRepository.Companion.eventRepository

class UpdateEventFragment : Fragment() {

    private lateinit var binding: FragmentUpdateEventBinding
    private var eventId = 0

    companion object {
        val updateEventFragment = UpdateEventFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentUpdateEventBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = GONE

        binding.btnUpdateEvent.setOnClickListener {
            binding.progressBar.visibility = VISIBLE
            val title = binding.etEventActivity.text.toString()
            val date = binding.etDate.text.toString()
            val time = binding.etTime.text.toString()
            val location = binding.etLocation.text.toString()
            val information = binding.etInformation.text.toString()

            if (verifyEventUserInputs(title, date, time, location, information)) {
                binding.progressBar.visibility = VISIBLE
                eventRepository.updateEvent(
                    title,
                    date,
                    time,
                    location,
                    information,
                    eventId
                ).addOnSuccessListener {
                    binding.progressBar.visibility = GONE
                    (context as MainActivity).changeToFragment(TAG_FRAGMENT_EVENTS)
                    binding.etInformation.setText("")
                    binding.etDate.setText("")
                    binding.etEventActivity.setText("")
                    binding.etTime.setText("")
                    binding.etLocation.setText("")
                }.addOnFailureListener {
                    binding.progressBar.visibility = GONE
                    // notify user about error
                    (context as MainActivity).makeToast(getString(R.string.failed_update_event))
                }
            } else {
                binding.progressBar.visibility = GONE
            }
        }

        binding.btnUpdateEventBack.setOnClickListener {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_EVENTS)
        }
    }

    fun clickedEvent(id: Int) {
        eventId = id

        eventRepository.getEventById(id) { result, event ->
            when (result) {
                "successful" -> {
                    binding.etDate.setText(event.date)
                    binding.etEventActivity.setText(event.title)
                    binding.etInformation.setText(event.information)
                    binding.etLocation.setText(event.location)
                    binding.etTime.setText(event.time)
                    binding.progressBar.visibility = GONE
                }
                "internalError" -> {
                    binding.etDate.setText("")
                    binding.etEventActivity.setText("")
                    binding.etInformation.setText("")
                    binding.etLocation.setText("")
                    binding.etTime.setText("")
                    binding.progressBar.visibility = GONE
                }
            }
        }
    }

    private fun verifyEventUserInputs(
        title: String,
        date: String,
        time: String,
        location: String,
        information: String
    ): Boolean {
        binding.textInputLayoutUpdateEventTitle.error = ""
        binding.textInputLayoutUpdateEventDate.error = ""
        binding.textInputLayoutUpdateEventTime.error = ""
        binding.textInputLayoutUpdateEventLocation.error = ""
        binding.textInputLayoutUpdateEventInformation.error = ""

        if (title.isEmpty()) {
            binding.textInputLayoutUpdateEventTitle.error = getString(R.string.empty_title)
            return false
        }

        if (date.isEmpty()) {
            binding.textInputLayoutUpdateEventDate.error = getString(R.string.empty_date)
            return false
        }

        if (time.isEmpty()) {
            binding.textInputLayoutUpdateEventTime.error = getString(R.string.empty_time)
            return false
        }

        if (location.isEmpty()) {
            binding.textInputLayoutUpdateEventLocation.error = getString(R.string.empty_location)
            return false
        }

        if (information.isEmpty()) {
            binding.textInputLayoutUpdateEventInformation.error =
                getString(R.string.empty_information)
            return false
        }

        return true
    }
}

