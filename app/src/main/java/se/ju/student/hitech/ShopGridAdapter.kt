package se.ju.student.hitech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

//class ShopGridAdapter(val shopItems: List<ShopItem>) :
class ShopGridAdapter(val shopItems: Array<String>) :
    RecyclerView.Adapter<ShopGridAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val shopImage : ImageView = itemView.findViewById(R.id.imageView_shop)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // Picasso.get().load(shopItems[position].imageUrl).into(holder.shopImage)
        Picasso.get().load(shopItems[position]).into(holder.shopImage)
    }

    override fun getItemCount(): Int {
        return shopItems.size
    }

}