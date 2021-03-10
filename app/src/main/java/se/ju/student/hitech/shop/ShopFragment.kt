package se.ju.student.hitech.shop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.ju.student.hitech.databinding.FragmentShopBinding
import se.ju.student.hitech.databinding.GridItemViewBinding

class ShopFragment : Fragment() {

    lateinit var binding: FragmentShopBinding
    private val viewModel: ShopViewModel by viewModels()

    companion object {
        fun newInstance() = ShopFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentShopBinding.inflate(layoutInflater, container, false).run {
        binding = this
        root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.shopItems.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.recyclerViewShopItems.post {

                    binding.recyclerViewShopItems.apply {
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = ShopAdapter(it)
                    }

                    binding.buttonOrder.setOnClickListener {
                        // open HI SHOP google form
                        openNewTabWindow("https://forms.gle/Mh4ALSQLNcTivKtj8", this)
                    }

                    binding.progressbarShop.visibility = View.GONE
                }

            }
        }

    }

    private fun openNewTabWindow(urls: String, context: ShopFragment) {
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

    class ShopViewHolder(val binding: GridItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    class ShopAdapter(private val shopItems: List<ShopItem>) :
        RecyclerView.Adapter<ShopViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ShopViewHolder(
            GridItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
            Picasso.get().load(shopItems[position].imageUrl).into(holder.binding.imageViewShop)
        }

        override fun getItemCount() = shopItems.size
    }
}