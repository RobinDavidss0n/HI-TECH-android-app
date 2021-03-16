package se.ju.student.hitech.chat.fragments

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.R
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
                        adapter = ContactAdapter(context ,it)
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
            chatRepository.loadAllMessagesFromSpecificChatAndUpdateIfChanged("HLafoEIXF2ui6aFJsYm9") { result, list ->
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

    class ContactAdapter(private val context: Context, private val currentMessages: List<se.ju.student.hitech.chat.Message>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            const val VIEW_TYPE_ONE = 1
            const val VIEW_TYPE_TWO = 2
        }

        private inner class ContactViewHolderRight(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var messageRight: TextView = itemView.findViewById(R.id.messageRight)
            var timeRight: TextView = itemView.findViewById(R.id.timeRight)
            fun bind(position: Int) {
                val currentMessage = currentMessages[position]
                messageRight.text = currentMessage.msgText
                timeRight.text = currentMessage.timestamp?.convertTimeToStringHourMinutesFormat()
            }
        }

        private inner class ContactViewHolderLeft(itemView: View) :
            RecyclerView.ViewHolder(itemView) {



            var messageLeft: TextView = itemView.findViewById(R.id.messageLeft)
            var timeLeft: TextView = itemView.findViewById(R.id.timeLeft)
            fun bind(position: Int) {
                val currentMessage = currentMessages[position]
                messageLeft.text = currentMessage.msgText
                timeLeft.text = currentMessage.timestamp?.convertTimeToStringHourMinutesFormat()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_ONE) {
                ContactViewHolderRight(
                    LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false)
                )
            }else{
                ContactViewHolderLeft(
                    LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false)
                )
            }

        }

        override fun getItemCount() = currentMessages.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (currentMessages[position].sentFromAdmin) {
                (holder as ContactViewHolderRight).bind(position)
            } else {
                (holder as ContactViewHolderLeft).bind(position)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (currentMessages[position].sentFromAdmin){
                1
            }else{
                2

            }
        }

    }
}