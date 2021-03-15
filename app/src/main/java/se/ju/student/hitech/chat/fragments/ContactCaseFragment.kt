package se.ju.student.hitech.chat.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.UserRepository
import se.ju.student.hitech.chat.ChatRepository
import se.ju.student.hitech.databinding.FragmentContactCaseBinding

class ContactCaseFragment : Fragment() {
    lateinit var binding: FragmentContactCaseBinding
    private val chatRepository = ChatRepository()

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
                binding.btnCase1.isEnabled =
                    binding.localUsername.length() > 0
                binding.btnCase2.isEnabled =
                    binding.localUsername.length() > 0
                binding.btnCase3.isEnabled =
                    binding.localUsername.length() > 0
                binding.btnCase4.isEnabled =
                    binding.localUsername.length() > 0

            }

        })

        binding.btnCase1.setOnClickListener {

            createNewChat("case1")
        }

        binding.btnCase2.setOnClickListener {
            createNewChat("case2")
        }

        binding.btnCase3.setOnClickListener {
            createNewChat("case3")
        }

        binding.btnCase4.setOnClickListener {
            createNewChat("case4")
        }


    }

    @SuppressLint("HardwareIds")
    fun createNewChat(case: String){

        val androidID = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val localUsername = binding.localUsername.text.toString()
        chatRepository.createNewChat(androidID,localUsername,case) { result ->
            when (result) {
                "successful" -> {
                    (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)
                }
                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
            }
        }

    }



}