package se.ju.student.hitech.events

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_EVENTS
import se.ju.student.hitech.databinding.FragmentCreateEventBinding
import se.ju.student.hitech.databinding.FragmentUpdateEventBinding
import se.ju.student.hitech.events.EventRepository.Companion.eventRepository

class UpdateEventFragment : Fragment() {

    lateinit var binding: FragmentUpdateEventBinding

    companion object {
        fun newInstance() = UpdateEventFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentUpdateEventBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.etEventActivity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnUpdateEvent.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnUpdateEvent.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdateEvent.isEnabled =
                    binding.etEventActivity.length() > 0 && binding.etLocation.length() > 0 && binding.etDate.length() > 0 && binding.etTime.length() > 0 && binding.etInformation.length() > 0

            }

        })

        binding.etDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnUpdateEvent.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnUpdateEvent.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdateEvent.isEnabled =
                    binding.etEventActivity.length() > 0 && binding.etLocation.length() > 0 && binding.etDate.length() > 0 && binding.etTime.length() > 0 && binding.etInformation.length() > 0

            }

        })

        binding.etTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnUpdateEvent.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnUpdateEvent.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdateEvent.isEnabled =
                    binding.etEventActivity.length() > 0 && binding.etLocation.length() > 0 && binding.etDate.length() > 0 && binding.etTime.length() > 0 && binding.etInformation.length() > 0

            }

        })

        binding.etLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnUpdateEvent.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnUpdateEvent.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdateEvent.isEnabled =
                    binding.etEventActivity.length() > 0 && binding.etLocation.length() > 0 && binding.etDate.length() > 0 && binding.etTime.length() > 0 && binding.etInformation.length() > 0

            }

        })

        binding.etInformation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnUpdateEvent.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnUpdateEvent.isEnabled = count > 0

            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnUpdateEvent.isEnabled =
                    binding.etEventActivity.length() > 0 && binding.etLocation.length() > 0 && binding.etDate.length() > 0 && binding.etTime.length() > 0 && binding.etInformation.length() > 0
            }

        })

        binding.btnUpdateEvent.setOnClickListener {
            val title = binding.etEventActivity.text
            val date = binding.etDate.text
            val time = binding.etTime.text
            val location = binding.etLocation.text
            val information = binding.etInformation.text

            eventRepository.updateEvent(
                title.toString(),
                date.toString(),
                time.toString(),
                location.toString(),
                information.toString(),
                id
            )
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_EVENTS)
        }

        binding.btnUpdateEventBack.setOnClickListener {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_EVENTS)
        }
    }
}

