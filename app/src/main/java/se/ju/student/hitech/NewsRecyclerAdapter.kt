package se.ju.student.hitech

import android.icu.text.CaseMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsRecyclerAdapter (private var newsTitles: List<String>,private var newsImages:List<Int>):
RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>(){
    inner class ViewHolder (itemView:View): RecyclerView.ViewHolder(itemView){

        var itemImage: ImageView = itemView.findViewById(R.id.news_image)
        var itemTitle: TextView = itemView.findViewById(R.id.news_title)

        init {
            itemView.setOnClickListener{ v: View->
                val position: Int = adapterPosition

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_news,parent, false)
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

