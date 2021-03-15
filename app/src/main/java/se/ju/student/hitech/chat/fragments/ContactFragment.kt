package se.ju.student.hitech.chat.fragments

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.UserRepository
import se.ju.student.hitech.chat.Chat
import se.ju.student.hitech.chat.ChatRepository
import se.ju.student.hitech.databinding.FragmentContactBinding
import se.ju.student.hitech.databinding.ItemChatRightBinding
import se.ju.student.hitech.databinding.ItemUserChatBinding
import se.ju.student.hitech.handlers.convertTimeToStringHourMinutesFormat

class ContactFragment : Fragment() {
    lateinit var binding: FragmentContactBinding
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentContactBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //binding.progressBarActiveChats.visibility = View.VISIBLE

        binding.rvRecyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.currentMessages.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.rvRecyclerViewMessages.post {

                    binding.rvRecyclerViewMessages.apply {
                        adapter = ContactAdapter(it)
                        adapter?.notifyDataSetChanged()
                    }
                }

            }

            //binding.progressBarActiveChats.visibility = View.GONE
        }


    }


    class ContactViewModel : ViewModel() {
        var chatRepository = ChatRepository()
        var currentMessages = MutableLiveData<List<se.ju.student.hitech.chat.Message>>()

        init {
            chatRepository.loadAllMessagesFromSpecificChatAndUpdateIfChanged(chatRepository.getCurrentChatID()) { result, list ->
                when (result) {
                    "successful" -> {
                        currentMessages.postValue(list)
                    }
                    "internalError" -> {
                        //notify user about error
                        Log.d("Error fireStore", "Error loading activeChat list from fireStore")
                    }
                }

            }

        }

    }

    class ContactViewHolderRight(val bindingRight: ItemChatRightBinding) :
        RecyclerView.ViewHolder(bindingRight.root)

    class ContactViewHolderLeft(val bindingLeft: ItemChatRightBinding) :
        RecyclerView.ViewHolder(bindingLeft.root)

    class ContactAdapter(private val currentMessages: List<se.ju.student.hitech.chat.Message>) :
        RecyclerView.Adapter<ContactViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(
            ItemChatRightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val message = currentMessages[position]

            if (message.sentFromAdmin && UserRepository().checkIfLoggedIn()){
                holder.binding.messageRight.text = message.msgText
                holder.binding.timeRight.text = message.timestamp?.convertTimeToStringHourMinutesFormat()
            }else{
                holder.binding.messageRight.text = message.msgText
                holder.binding.timeRight.text = message.timestamp?.convertTimeToStringHourMinutesFormat()
            }


        }

        override fun getItemCount() = currentMessages.size

    }
}