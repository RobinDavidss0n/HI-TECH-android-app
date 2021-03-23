package se.ju.student.hitech.chat

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
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
import se.ju.student.hitech.databinding.FragmentContactBinding
import se.ju.student.hitech.handlers.convertTimeToStringHourMinutesFormat
import se.ju.student.hitech.user.UserRepository
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
        updateAppearanceForCurrentUser()

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
            .setTitle(getString(R.string.close_chat))
            .setMessage(getString(R.string.close_chat_confirmation))
            .setPositiveButton(
                getString(R.string.yes)

            ) { _, _ ->
                binding.progressbarContact.visibility = VISIBLE
                chatRepository.closeChat(currentChatID) { result ->
                    when (result) {

                        "successful" -> {
                            if (UserRepository().checkIfLoggedIn()) {

                                (context as MainActivity).changeToFragment(
                                    MainActivity.TAG_FRAGMENT_CONTACT_USER_VIEW
                                )

                            } else {
                                (context as MainActivity).changeToFragment(
                                    MainActivity.TAG_FRAGMENT_CONTACT_CASE
                                )
                            }
                        }
                        "internalError" -> {
                            binding.progressbarContact.visibility = GONE
                            (context as MainActivity).makeToast(getString(R.string.internalError))
                        }
                    }
                }
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }
            .show()
    }

    private fun leaveOrJoinChat() {

        chatRepository.isChatOccupied(currentChatID) { result ->
            when (result) {
                "false" -> {
                    AlertDialog.Builder(context as MainActivity)
                        .setTitle(getString(R.string.join_chat))
                        .setMessage(getString(R.string.join_chat_confirmation))
                        .setPositiveButton(
                            getString(R.string.yes)
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
                                                    updateAppearanceForCurrentUser()
                                                    binding.progressbarContact.visibility = GONE

                                                }

                                                "internalError" -> {
                                                    binding.progressbarContact.visibility = GONE
                                                    (context as MainActivity).makeToast(getString(R.string.internalError))
                                                }
                                            }
                                        }
                                    }
                                    "internalError" -> {
                                        binding.progressbarContact.visibility = GONE
                                        (context as MainActivity).makeToast(getString(R.string.internalError))
                                    }
                                }
                            }
                        }
                        .setNegativeButton(
                            getString(R.string.no)
                        ) { _, _ -> }
                        .show()
                }
                "true" -> {
                    AlertDialog.Builder(context as MainActivity)

                        .setTitle(getString(R.string.leave_chat))
                        .setMessage(getString(R.string.leave_chat_confirmation))
                        .setPositiveButton(
                            getString(R.string.yes)

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
                                            "internalError" -> (context as MainActivity).makeToast(
                                                getString(R.string.internalError)
                                            )
                                        }
                                        binding.progressbarContact.visibility = GONE
                                    }

                                    "internalError" -> {
                                        binding.progressbarContact.visibility = GONE
                                        (context as MainActivity).makeToast(getString(R.string.internalError))
                                    }
                                }
                            }
                        }
                        .setNegativeButton(
                            getString(R.string.no)
                        ) { _, _ -> }
                        .show()
                }
                "internalError" -> {
                    (context as MainActivity).makeToast(getString(R.string.internalError))
                    binding.progressbarContact.visibility = GONE
                }
            }
        }
    }

    private fun updateAppearanceForCurrentUser() {
        chatRepository.getChatWithChatID(currentChatID) { result, chat ->
            when (result) {
                "successful" -> {
                    if (userRepository.checkIfLoggedIn()) {
                        binding.chattingWith.text =
                            getString(R.string.chatting_With, chat.localUsername)

                        chatRepository.checkIfCurrentAdminIsInChatOrIfEmpty(
                            userRepository.getUserID(),
                            currentChatID
                        ) { result2 ->
                            when (result2) {
                                "true" -> {
                                    binding.sendMessageLayout.visibility = VISIBLE
                                    binding.leaveOrJoinChat.visibility = VISIBLE
                                    binding.leaveOrJoinChat.text = getString(R.string.leave_chat)
                                    binding.closeChat.visibility = VISIBLE
                                }
                                "empty" -> {
                                    binding.leaveOrJoinChat.visibility = VISIBLE
                                    binding.leaveOrJoinChat.text = getString(R.string.join_chat)
                                }
                                "false" -> {
                                    binding.currentAdmin.visibility = VISIBLE
                                    binding.currentAdmin.text =
                                        getString(R.string.is_chatting_here, chat.adminUsername)
                                    binding.sendMessageLayout.visibility = GONE
                                }
                                "internalError" -> (context as MainActivity).makeToast(getString(R.string.error_chat))
                            }
                            binding.progressbarContact2.visibility = GONE
                        }
                    } else {
                        binding.sendMessageLayout.visibility = VISIBLE
                        binding.closeChat.visibility = VISIBLE
                        if (chat.adminUsername == "") {
                            binding.chattingWith.text =
                                getString(R.string.wating_for_admin)

                        } else {
                            binding.chattingWith.text =
                                getString(R.string.chatting_With, chat.adminUsername)
                        }
                        binding.progressbarContact2.visibility = GONE
                    }
                }
                "internalError" -> {
                    binding.progressbarContact2.visibility = GONE
                    (context as MainActivity).makeToast(getString(R.string.error_chat))
                }
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
                        val errorList = mutableListOf<Message>()
                        val error = Message()
                        //Can't use getString() in ViewModel so that's why it's hard coded
                        error.msgText =
                            "Error getting messages, check your internet connection and restart the app."
                        errorList.add(error)
                        viewModel.currentMessages.postValue(errorList)
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
                    }

                    "internalError" -> {
                        (context as MainActivity).makeToast(getString(R.string.internalError))
                    }
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