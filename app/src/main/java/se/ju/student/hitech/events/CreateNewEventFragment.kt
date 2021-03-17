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
import se.ju.student.hitech.databinding.FragmentCreateEventBinding
import se.ju.student.hitech.events.EventRepository.Companion.eventRepository

class CreateNewEventFragment : Fragment() {

    lateinit var binding: FragmentCreateEventBinding

    companion object {
        fun newInstance() = CreateNewEventFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCreateEventBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = GONE

        binding.btnCreateEvent.setOnClickListener {

            val title = binding.etEventActivity.text.toString()
            val date = binding.etDate.text.toString()
            val time = binding.etTime.text.toString()
            val location = binding.etLocation.text.toString()
            val information = binding.etInformation.text.toString()

            if (verifyEventUserInputs(title, date, time, location, information)) {
                binding.progressBar.visibility = VISIBLE
                eventRepository.addEvent(
                    title,
                    date,
                    time,
                    location,
                    information
                ) { result ->
                    when (result) {
                        "successful" -> {
                            binding.progressBar.visibility = GONE
                            (context as MainActivity).changeToFragment(TAG_FRAGMENT_EVENTS)
                        }
                        "internalError" -> {
                            binding.progressBar.visibility = GONE
                            (context as MainActivity).makeToast(getString(R.string.failed_create_event))
                        }
                    }
                }
            } else {
                binding.progressBar.visibility = GONE
            }
        }

        binding.btnCreateEventBack.setOnClickListener {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_EVENTS)
        }
    }

    private fun verifyEventUserInputs(
        title: String,
        date: String,
        time: String,
        location: String,
        information: String
    ): Boolean {
        binding.textInputLayoutNewEventTitle.error = ""
        binding.textInputLayoutNewEventDate.error = ""
        binding.textInputLayoutNewEventTime.error = ""
        binding.textInputLayoutNewEventLocation.error = ""
        binding.textInputLayoutNewEventInformation.error = ""

        if (title.isEmpty()) {
            binding.textInputLayoutNewEventTitle.error = getString(R.string.empty_title)
            return false
        }

        if (date.isEmpty()) {
            binding.textInputLayoutNewEventDate.error = getString(R.string.empty_date)
            return false
        }

        if (time.isEmpty()) {
            binding.textInputLayoutNewEventTime.error = getString(R.string.empty_time)
            return false
        }

        if (location.isEmpty()) {
            binding.textInputLayoutNewEventLocation.error = getString(R.string.empty_location)
            return false
        }

        if (information.isEmpty()) {
            binding.textInputLayoutNewEventInformation.error = getString(R.string.empty_information)
            return false
        }

        return true
    }
}