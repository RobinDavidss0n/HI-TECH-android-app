package se.ju.student.hitech

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_CREATE_NEW_EVENT
import se.ju.student.hitech.databinding.FragmentEventsBinding
import se.ju.student.hitech.databinding.FragmentShopBinding
import se.ju.student.hitech.databinding.ItemEventBinding

class EventsFragment : Fragment() {

    lateinit var binding: FragmentEventsBinding
    private val viewModel: EventsViewModel by viewModels()

    companion object {
        fun newInstance() = ShopFragment()
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

        viewModel.events.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.rvEvents.post {

                    binding.rvEvents.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = EventAdapter(it)
                        registerForContextMenu(this)
                    }


                    binding.swipeRefreshEvents.setOnRefreshListener {
                        eventRepository.updateEventList()
                        binding.swipeRefreshEvents.isRefreshing = false
                    }
                    binding.fabCreateEvent.setOnClickListener {
                        (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEW_EVENT)
                    }

                    binding.pbEvent.visibility = View.GONE
                }

            }
        }

        if (userRepository.checkIfLoggedIn()) {
            binding.fabCreateEvent.visibility = VISIBLE
        } else {
            binding.fabCreateEvent.visibility = GONE
        }
    }

    class EventsViewModel : ViewModel() {
        var events = eventRepository.events

        init {
            viewModelScope.launch(Dispatchers.IO) {

                eventRepository.loadEventData(events) { fetchedEvents, events ->
                    events.postValue(fetchedEvents)
                }
            }
        }

    }

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    class EventAdapter(val events: List<Event>) : RecyclerView.Adapter<EventViewHolder>() {

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

            if (userRepository.checkIfLoggedIn()) {
                holder.binding.icMenu.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, holder.binding.icMenu)
                    popupMenu.inflate(R.menu.recyclerview_menu)

                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_delete -> {
                                eventRepository.deleteEvent(position)
                            }
                            R.id.menu_edit -> {
                                eventRepository.updateEvent()
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



