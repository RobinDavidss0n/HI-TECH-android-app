package se.ju.student.hitech

import android.widget.GridView

class ShopGridAdapter(var shopItems: List<ShopItem>) :
    GridView.Adapter<ShopGridAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.news_image)
        var itemTitle: TextView = itemView.findViewById(R.id.news_title)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_news_image, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = newsTitles[position]
        holder.itemImage.setImageResource(newsImages[position])
    }

    override fun getItemCount(): Int {
        return shopItems.size
    }
}