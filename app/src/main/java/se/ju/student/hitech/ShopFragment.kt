package se.ju.student.hitech

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShopFragment : Fragment() {

    val shopImages = MutableLiveData<List<ShopItem>>()
    //  private var shopGridAdapter: ShopGridAdapter(shopImages)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_shop, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.findViewById<Button>(R.id.button_order)?.setOnClickListener {
            // open HI SHOP google form
            openNewTabWindow("https://forms.gle/Mh4ALSQLNcTivKtj8", this)
        }

        var recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView_shopItems)
      //  shopImages = shopRepository.loadShopImages()

        shopRepository.loadShopImages(shopImages) { fetchedImages, shopImages ->
            shopImages.postValue(fetchedImages)
        }

       // val adapter = ShopGridAdapter(shopImages)
        val gridLayout = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = gridLayout
     //   recyclerView?.adapter = adapter


    /*    var recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView_shopItems)
        val images : Array<String> = resources.getStringArray(R.array.images)
        val adapter = ShopGridAdapter(images)
        val gridLayout = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = gridLayout
        recyclerView?.adapter = adapter */

    }

    private fun openNewTabWindow(urls: String, context: ShopFragment) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
}