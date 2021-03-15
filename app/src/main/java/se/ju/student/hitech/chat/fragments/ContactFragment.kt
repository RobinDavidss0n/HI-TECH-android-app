package se.ju.student.hitech.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.ju.student.hitech.R
import se.ju.student.hitech.UserRepository
import se.ju.student.hitech.databinding.FragmentActiveChatsAdminBinding
import se.ju.student.hitech.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    lateinit var binding: FragmentContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentContactBinding.inflate(layoutInflater, container, false).run{
        binding = this
        root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val userRepository = UserRepository()
        if(userRepository.checkIfLoggedIn()){
            binding.messageInput.hint= "Answer..."
        }

    }
}

