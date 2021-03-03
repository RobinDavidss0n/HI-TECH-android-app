package se.ju.student.hitech

import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.ju.student.hitech.databinding.CardNewsBinding
import se.ju.student.hitech.databinding.FragmentNewsBinding
import kotlin.concurrent.thread

class test : Fragment() {

    lateinit var binding: FragmentNewsBinding
    val viewModel: ShopViewModel by viewModels()

    companion object {
        fun newInstance() = test()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNewsBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.shopItems.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.rvRecyclerView.post {

                    binding.rvRecyclerView.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ShopAdapter(it)
                    }

                    view?.findViewById<Button>(R.id.button_order)?.setOnClickListener {
                        // open HI SHOP google form
                        openNewTabWindow("https://forms.gle/Mh4ALSQLNcTivKtj8", this)
                    }

                    binding.progressBar.visibility = View.GONE
                }

            }
        }

    }

    private fun openNewTabWindow(urls: String, context: test) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    class ShopViewModel : ViewModel() {

        val shopItems = MutableLiveData<List<ShopItem>>()

        init {
            viewModelScope.launch(Dispatchers.IO) {

                shopRepository.loadShopImages(shopItems) { fetchedImages, shopItems ->
                    shopItems.postValue(fetchedImages)
                }

            }
        }

    }

    class ShopViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    class ShopAdapter(private val shopItems: List<ShopItem>) :
        RecyclerView.Adapter<ShopViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ShopViewHolder(
            CardNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
            Picasso.get().load(shopItems[position].imageUrl).into(holder.shopImage)
        }

        override fun getItemCount() = shopItems.size
    }

}