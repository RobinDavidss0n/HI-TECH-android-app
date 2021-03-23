package se.ju.student.hitech.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_CREATE_NEW_EVENT
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_UPDATE_EVENT
import se.ju.student.hitech.R
import se.ju.student.hitech.databinding.FragmentEventsBinding
import se.ju.student.hitech.databinding.ItemEventBinding
import se.ju.student.hitech.dialogs.DeleteEventAlertDialog
import se.ju.student.hitech.dialogs.DeleteEventAlertDialog.Companion.TAG_DELETE_EVENT_DIALOG
import se.ju.student.hitech.events.EventRepository.Companion.eventRepository
import se.ju.student.hitech.user.UserRepository

class EventsFragment : Fragment() {

    lateinit var binding: FragmentEventsBinding
    var userRepository = UserRepository()
    private val viewModel: EventsViewModel by viewModels()

    companion object {
        fun newInstance() = EventsFragment()
        var loggedIn: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEventsBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
            registerForContextMenu(this)
        }

        val manager = parentFragmentManager

        viewModel.events.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvEvents.post {
                    binding.rvEvents.apply {
                        adapter = EventAdapter(it, manager)
                        adapter?.notifyDataSetChanged()
                    }
                }
                binding.progressBar.visibility = GONE
            }
        }

        loggedIn = userRepository.checkIfLoggedIn()

        if (loggedIn) {
            binding.fabCreateEvent.visibility = VISIBLE
        } else {
            binding.fabCreateEvent.visibility = GONE
        }

        binding.fabCreateEvent.setOnClickListener {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEW_EVENT)
        }
    }

    class EventsViewModel : ViewModel() {
        var events = MutableLiveData<List<Event>>()

        init {
            eventRepository.listenForEventChanges { result, list ->
                when (result) {
                    "successful" -> {
                        events.postValue(list.asReversed())

                    }
                    "internalError" -> {
                        val errorList = mutableListOf<Event>()
                        val error = Event()
                        //Can't use getString() in ViewModel so that's why it's hard coded
                        error.title =
                            "Error getting events, check your internet connection and restart the app."
                        errorList.add(error)
                        events.postValue(errorList)
                    }
                }
            }
        }
    }

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    class EventAdapter(private val events: List<Event>, private val manager: FragmentManager?) :
        RecyclerView.Adapter<EventViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventViewHolder(
            ItemEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
            val event = events[position]
            holder.binding.tvDate.text = event.date
            holder.binding.tvLocation.text = event.location
            holder.binding.tvTime.text = event.time
            holder.binding.tvTitle.text = event.title
            holder.binding.tvInformation.text = event.information

            val id = event.id

            if (loggedIn) {
                holder.binding.icMenu.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, holder.binding.icMenu)
                    popupMenu.inflate(R.menu.recyclerview_menu)

                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_delete -> {
                                if (manager != null) {
                                    val deleteEventDialog = DeleteEventAlertDialog()
                                    // pass the clicked id to the DialogFragment
                                    var argument = Bundle()
                                    argument.putInt("event_id", id)
                                    deleteEventDialog.arguments = argument
                                    deleteEventDialog.show(manager, TAG_DELETE_EVENT_DIALOG)
                                }
                            }
                            R.id.menu_edit -> {
                                (holder.itemView.context as MainActivity).setClickedEventId(id)
                                (holder.itemView.context as MainActivity).changeToFragment(
                                    TAG_FRAGMENT_UPDATE_EVENT
                                )
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
            } else {
                holder.binding.icMenu.visibility = GONE
            }
        }

        override fun getItemCount() = events.size
    }
}



