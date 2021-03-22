package se.ju.student.hitech.chat

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import se.ju.student.hitech.chat.ChatRepository.Companion.chatRepository
import se.ju.student.hitech.user.UserRepository
import se.ju.student.hitech.databinding.FragmentContactBinding
import se.ju.student.hitech.handlers.convertTimeToStringHourMinutesFormat
import se.ju.student.hitech.user.UserRepository.Companion.userRepository

class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private val viewModel: ContactViewModel by viewModels()
    private lateinit var messagesAdapter: ContactAdapter
    private lateinit var currentChatID: String

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
        currentChatID = chatRepository.getCurrentChatID()

        binding.rvRecyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.currentMessages.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.rvRecyclerViewMessages.post {

                    binding.rvRecyclerViewMessages.apply {
                        adapter = ContactAdapter(context, it)
                        adapter?.notifyDataSetChanged()
                        messagesAdapter = adapter as ContactAdapter
                    }
                }

                binding.progressbarContact.visibility = GONE
            }
            viewModel.currentMessages.value?.let { it1 ->
                binding.rvRecyclerViewMessages.scrollToPosition(it1.size - 1)
            }
        }

        loadAllMessagesAndUpdateIfChanged()
        setText()

        binding.sendMessage.setOnClickListener {
            sendMessage()
        }

        binding.leaveOrJoinChat.setOnClickListener {
            leaveOrJoinChat()
        }

        binding.closeChat.setOnClickListener {
            closeChat()
        }
    }

    private fun closeChat() {
        AlertDialog.Builder(context as MainActivity)
            .setTitle("Close chat")
            .setMessage("Do you want to close the chat?")
            .setPositiveButton(
                "Yes"

            ) { _, _ ->
                binding.progressbarContact.visibility = VISIBLE
                chatRepository.closeChat(currentChatID) { result ->
                    when (result) {

                        "successful" -> {
                            userRepository.removeChatFromUser(currentChatID) { it2 ->
                                when (it2) {
                                    "successful" -> {
                                        (context as MainActivity).changeToFragment(
                                            MainActivity.TAG_FRAGMENT_CONTACT_USER_VIEW
                                        )
                                    }
                                    "internalError" -> (context as MainActivity).makeToast("error")
                                }
                                binding.progressbarContact.visibility = GONE
                            }
                        }

                        "internalError" -> {
                            binding.progressbarContact.visibility = GONE
                            (context as MainActivity).makeToast("error")
                        }
                    }
                }
            }
            .setNegativeButton(
                "NO"
            ) { _, _ ->
                //Don't delete
            }
            .show()
    }

    private fun leaveOrJoinChat() {

        chatRepository.isChatOccupied(currentChatID) { result ->
            when (result) {
                "false" -> {
                    AlertDialog.Builder(context as MainActivity)
                        .setTitle("Join chat")
                        .setMessage("Do you want to join the chat?")
                        .setPositiveButton(
                            "Yes"

                        ) { _, _ ->
                            binding.progressbarContact.visibility = VISIBLE
                            userRepository.getUsername(userRepository.getUserID()) { result, username ->
                                when (result) {
                                    "successful" -> {
                                        chatRepository.addAdminToChat(
                                            userRepository.getUserID(),
                                            username,
                                            currentChatID
                                        ) {
                                            when (it) {
                                                "successful" -> {

                                                    setText()
                                                    userRepository.addChatToUser(
                                                        currentChatID
                                                    ) { it2 ->
                                                        when (it2) {
                                                            "successful" -> {
                                                                setText()
                                                            }
                                                            "internalError" -> (context as MainActivity).makeToast(
                                                                "error"
                                                            )
                                                        }
                                                        binding.progressbarContact.visibility = GONE
                                                    }
                                                }

                                                "internalError" -> {
                                                    binding.progressbarContact.visibility =
                                                        View.GONE
                                                    (context as MainActivity).makeToast("error")
                                                }
                                            }
                                        }
                                    }
                                    "internalError" -> {
                                        binding.progressbarContact.visibility = GONE
                                        (context as MainActivity).makeToast("error")
                                    }
                                }
                            }
                        }
                        .setNegativeButton(
                            "NO"
                        ) { _, _ ->
                            //Don't delete
                        }
                        .show()
                }
                "true" -> {
                    AlertDialog.Builder(context as MainActivity)
                        .setTitle("Leave chat")
                        .setMessage("Do you want to leave the chat?")
                        .setPositiveButton(
                            "Yes"

                        ) { _, _ ->
                            binding.progressbarContact.visibility = VISIBLE
                            chatRepository.removeAdminFromChat(currentChatID) {
                                when (it) {
                                    "successful" -> userRepository.removeChatFromUser(currentChatID) { it2 ->
                                        when (it2) {
                                            "successful" -> {
                                                (context as MainActivity).changeToFragment(
                                                    MainActivity.TAG_FRAGMENT_CONTACT_USER_VIEW
                                                )
                                            }
                                            "internalError" -> (context as MainActivity).makeToast("error")
                                        }
                                        binding.progressbarContact.visibility = GONE
                                    }

                                    "internalError" -> {
                                        binding.progressbarContact.visibility = GONE
                                        (context as MainActivity).makeToast("error")
                                    }
                                }
                            }
                        }
                        .setNegativeButton(
                            "NO"
                        ) { _, _ ->
                            //Don't delete
                        }
                        .show()
                }
                "internalError" -> {
                    (context as MainActivity).makeToast("error")
                    binding.progressbarContact.visibility = GONE
                }
            }
        }
    }

    private fun setText() {
        chatRepository.getChatWithChatID(currentChatID) { result, chat ->
            when (result) {
                "successful" -> {
                    if (userRepository.checkIfLoggedIn()) {
                        binding.chattingWith.text = "Chatting With ${chat.localUsername}"

                        chatRepository.checkIfCurrentAdminIsInChatOrIfEmpty(
                            userRepository.getUserID(),
                            currentChatID
                        ) {
                            when (it) {
                                "true" -> {
                                    binding.leaveOrJoinChat.visibility = VISIBLE
                                    binding.leaveOrJoinChat.text = "Leave chat"
                                    binding.closeChat.visibility = VISIBLE
                                }
                                "empty" -> {
                                    binding.leaveOrJoinChat.visibility = VISIBLE
                                    binding.leaveOrJoinChat.text = "Join chat"
                                }
                                "false" -> {
                                    binding.currentAdmin.visibility = VISIBLE
                                    binding.currentAdmin.text =
                                        "${chat.adminUsername} is currently chatting here."
                                    binding.sendMessageLayout.visibility = GONE
                                }
                                "internalError" -> (context as MainActivity).makeToast("error")
                            }
                        }
                    } else {
                        binding.chattingWith.text = "Chatting With ${chat.adminUsername}"
                    }
                }
                "internalError" -> (context as MainActivity).makeToast("error")
            }
        }
    }

    private fun loadAllMessagesAndUpdateIfChanged() {

        if (currentChatID != "noChatSelected") {
            chatRepository.loadAllMessagesFromSpecificChatAndUpdateIfChanged(currentChatID) { result, firstFullList, newMessage ->
                when (result) {
                    "firstSetup" -> {
                        viewModel.messageList = firstFullList
                        viewModel.currentMessages.postValue(viewModel.messageList)

                    }
                    "newData" -> {
                        viewModel.messageList.add(newMessage)
                        messagesAdapter.notifyDataSetChanged()
                    }
                    "internalError" -> {
                        //notify user about error
                        Log.d("Error fireStore", "Error loading activeChat list from fireStore")
                    }
                }
                binding.progressbarContact.visibility = GONE
            }
        }
    }

    private fun sendMessage() {
        val msg = binding.messageInput.text.toString()

        if (msg.isNotEmpty()) {
            chatRepository.addMessage(
                msg,
                UserRepository().checkIfLoggedIn(),
                currentChatID
            ) { result ->
                when (result) {
                    "successful" -> {
                        binding.messageInput.text.clear()
                        viewModel.currentMessages.value?.let { it1 ->
                            binding.rvRecyclerViewMessages.scrollToPosition(it1.size - 1)
                        }
                        Log.d("FireStore", "Message sent.")
                    }
                    "internalError" -> (context as MainActivity).makeToast("Something went wrong, check your internet connection and try again.")
                }
            }
        }
    }

    class ContactViewModel : ViewModel() {
        var currentMessages = MutableLiveData<List<Message>>()

        lateinit var messageList: MutableList<Message>
    }

    class ContactAdapter(
        private val context: Context,
        private val currentMessages: List<Message>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_RIGHT = 1
            const val VIEW_TYPE_LEFT = 0
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
            return if (viewType == VIEW_TYPE_RIGHT) {
                ContactViewHolderRight(
                    LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false)
                )
            } else { // view type left
                ContactViewHolderLeft(
                    LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false)
                )
            }
        }

        override fun getItemCount() = currentMessages.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (currentMessages[position].sentFromAdmin && userRepository.checkIfLoggedIn()) {
                (holder as ContactViewHolderRight).bind(position)
            } else if (!currentMessages[position].sentFromAdmin && !userRepository.checkIfLoggedIn()) {
                (holder as ContactViewHolderRight).bind(position)
            } else {
                (holder as ContactViewHolderLeft).bind(position)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (currentMessages[position].sentFromAdmin && userRepository.checkIfLoggedIn()) {
                VIEW_TYPE_RIGHT
            } else if (!currentMessages[position].sentFromAdmin && !userRepository.checkIfLoggedIn()) {
                VIEW_TYPE_RIGHT
            } else {
                VIEW_TYPE_LEFT
            }
        }
    }
}