package se.ju.student.hitech.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
import se.ju.student.hitech.chat.ChatRepository.Companion.chatRepository
import se.ju.student.hitech.databinding.FragmentContactCaseBinding

class ContactCaseFragment : Fragment() {
    lateinit var binding: FragmentContactCaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentContactCaseBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.localUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnCaseEducation.isEnabled =
                    binding.localUsername.length() > 0
                binding.btnCaseEvents.isEnabled =
                    binding.localUsername.length() > 0
                binding.btnCaseWorkEnvironment.isEnabled =
                    binding.localUsername.length() > 0
                binding.btnCaseOther.isEnabled =
                    binding.localUsername.length() > 0
            }
        })

        binding.btnCaseEducation.setOnClickListener {
            createNewChat(getString(R.string.case_education))
        }

        binding.btnCaseEvents.setOnClickListener {
            createNewChat(getString(R.string.case_event))
        }

        binding.btnCaseWorkEnvironment.setOnClickListener {
            createNewChat(getString(R.string.case_work_environment))
        }

        binding.btnCaseOther.setOnClickListener {
            createNewChat(getString(R.string.case_other))
        }
    }

    private fun createNewChat(case: String) {
        binding.progressbarContactCase.visibility = View.VISIBLE
        chatRepository.getFirebaseInstallationsID { result, localID ->
            when (result) {
                "successful" -> {
                    val localUsername = binding.localUsername.text.toString()
                    chatRepository.createNewChat(localID, localUsername, case) { result2, chatID ->
                        when (result2) {
                            "successful" -> {
                                binding.progressbarContactCase.visibility = View.GONE
                                ChatRepository().setCurrentChatID(chatID)
                                (context as MainActivity).reloadContactFragment()
                                (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)
                            }
                            "internalError" -> {
                                binding.progressbarContactCase.visibility = View.GONE
                                (context as MainActivity).makeToast(getString(R.string.internalError))
                            }
                        }
                    }
                }
                "internalError" -> {
                    binding.progressbarContactCase.visibility = View.GONE
                    (context as MainActivity).makeToast(getString(R.string.internalError))
                }
            }
        }
    }
}