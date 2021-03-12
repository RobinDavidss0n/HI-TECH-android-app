package se.ju.student.hitech.chat.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
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

        chatRepository.createNewChat(androidID, case) { result ->
            when (result) {
                "successful" -> {
                    (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)
                }
                "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
            }
        }

    }



}