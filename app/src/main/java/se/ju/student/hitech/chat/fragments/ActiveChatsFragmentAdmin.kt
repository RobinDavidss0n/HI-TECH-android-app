package se.ju.student.hitech.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.ju.student.hitech.UserRepository
import se.ju.student.hitech.chat.Chat
import se.ju.student.hitech.chat.ChatRepository
import se.ju.student.hitech.databinding.FragmentActiveChatsAdminBinding


class ActiveChatsFragmentAdmin : Fragment() {
    lateinit var binding: FragmentActiveChatsAdminBinding
    //private val viewModel: ActiveChatsViewModel by viewModels()

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

/*
    class ActiveChatsViewModel : ViewModel() {
        var chatRepository = ChatRepository()
        var activeChats = MutableLiveData<List<Chat>>()

        init {
            val fetchedActiveCahts = chatRepository.getAllActiveChats(){result, data->

                when (result) {
                    "successful" -> {
                        activeChats.postValue()

                    }
                    "internalError" ->{

                    }
                }

                }
            }

        }



    }*/
}
