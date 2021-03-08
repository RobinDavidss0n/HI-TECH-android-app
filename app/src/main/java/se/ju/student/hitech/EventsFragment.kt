package se.ju.student.hitech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.ju.student.hitech.databinding.FragmentEventsBinding
import se.ju.student.hitech.databinding.FragmentShopBinding

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

        val floatingActionButton = view?.findViewById<FloatingActionButton>(R.id.fab_create_event)
        val progressBar = view?.findViewById<FloatingActionButton>(R.id.progressBar_event)

        if (userRepository.checkIfLoggedIn()) {
            floatingActionButton?.visibility = VISIBLE
        } else{
            floatingActionButton?.visibility = GONE
        }



    }

}

class EventsViewModel : ViewModel() {

    val events = MutableLiveData<List<Event>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {

            shopRepository.loadShopImages(shopItems) { fetchedImages, shopItems ->
                shopItems.postValue(fetchedImages)
            }

        }
    }

}