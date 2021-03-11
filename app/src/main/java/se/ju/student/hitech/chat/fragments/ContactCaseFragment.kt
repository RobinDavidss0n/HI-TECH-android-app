package se.ju.student.hitech.chat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import se.ju.student.hitech.MainActivity
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

        binding.btnCase1.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)
        }
        binding.btnCase2.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)
        }
        binding.btnCase3.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)

        }
        binding.btnCase4.setOnClickListener {
            (context as MainActivity).changeToFragment(MainActivity.TAG_FRAGMENT_CONTACT)

        }


    }



}