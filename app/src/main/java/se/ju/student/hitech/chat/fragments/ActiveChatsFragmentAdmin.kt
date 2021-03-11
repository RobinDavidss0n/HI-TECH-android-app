package se.ju.student.hitech.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.ju.student.hitech.databinding.FragmentActiveChatsAdminBinding


class ActiveChatsFragmentAdmin : Fragment() {
    lateinit var binding: FragmentActiveChatsAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentActiveChatsAdminBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
    }
}