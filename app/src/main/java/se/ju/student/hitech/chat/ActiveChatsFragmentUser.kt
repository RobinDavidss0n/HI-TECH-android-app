package se.ju.student.hitech.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_CONTACT
import se.ju.student.hitech.chat.ChatRepository.Companion.chatRepository
import se.ju.student.hitech.databinding.FragmentActiveChatsUserBinding
import se.ju.student.hitech.databinding.ItemUserChatBinding

class ActiveChatsFragmentUser : Fragment() {
    lateinit var binding: FragmentActiveChatsUserBinding
    private val viewModel: ActiveChatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentActiveChatsUserBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.progressBarActiveChats.visibility = VISIBLE

        binding.rvRecyclerViewActiveChats.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }

        viewModel.activeChats.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.rvRecyclerViewActiveChats.post {

                    binding.rvRecyclerViewActiveChats.apply {
                        adapter = ActiveChatsAdapter(it)
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
            binding.progressBarActiveChats.visibility = GONE
        }
    }

    class ActiveChatsViewModel : ViewModel() {
        var activeChats = MutableLiveData<List<Chat>>()

        init {
            chatRepository.loadAllChatsAndUpdateIfChanged { result, list ->
                when (result) {
                    "successful" -> {
                        activeChats.postValue(list)
                    }
                    "internalError" -> {
                        //notify user about error
                        Log.d("Error fireStore", "Error loading activeChat list from fireStore")
                    }
                }
            }
        }
    }

    class ActiveChatsViewHolder(val binding: ItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ActiveChatsAdapter(private val activeChats: List<Chat>) :
        RecyclerView.Adapter<ActiveChatsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ActiveChatsViewHolder(
            ItemUserChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: ActiveChatsViewHolder, position: Int) {
            val chat = activeChats[position]
            holder.binding.tvUsername.text = chat.localUsername
            holder.binding.tvCase.text = chat.case

            holder.binding.itemUserChat.setOnClickListener {
                ChatRepository().setCurrentChatID(chat.chatID.toString())
                (holder.itemView.context as MainActivity).reloadContactFragment()
                (holder.itemView.context as MainActivity).changeToFragment(TAG_FRAGMENT_CONTACT)
            }
        }

        override fun getItemCount() = activeChats.size
    }
}