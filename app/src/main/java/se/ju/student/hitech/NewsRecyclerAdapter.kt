package se.ju.student.hitech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NewsRecyclerAdapter(var news: List<Novelty>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val POST_TYPE_IMAGE: Int = 0
        private const val POST_TYPE_NO_IMAGE: Int = 1
    }

    // view holders for all types of items
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(novelty: Novelty) {
            itemView.findViewById<ImageView>(R.id.news_image).setImageResource(novelty.image)
            itemView.findViewById<TextView>(R.id.news_title).text = novelty.title
        }
    }

    class NoImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(novelty: Novelty) {
            itemView.findViewById<TextView>(R.id.news_title_no_image).text = novelty.title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (news[position].post_type == 0L) {
            POST_TYPE_IMAGE
        } else {
            POST_TYPE_NO_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == POST_TYPE_NO_IMAGE){
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_news, parent, false)
            return NoImageViewHolder(v)
        }
        else{
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_news_image, parent, false)
            return ImageViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == POST_TYPE_NO_IMAGE){
            (holder as NoImageViewHolder).bind(news[position])
        } else{
            (holder as ImageViewHolder).bind(news[position])
        }
    }
}


/*
class NewsRecyclerAdapter(private var newsTitles: List<String>, private var newsImages: List<Int>) :
    RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>() {
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
        return newsTitles.size
    }
}

*/