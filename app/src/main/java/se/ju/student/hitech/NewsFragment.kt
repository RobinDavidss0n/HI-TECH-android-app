package se.ju.student.hitech

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_CREATE_NEWS_POST
import se.ju.student.hitech.ViewNoveltyActivity.Companion.EXTRA_NOVELTY_ID
import se.ju.student.hitech.databinding.CardNewsBinding
import se.ju.student.hitech.databinding.FragmentNewsBinding
import kotlin.concurrent.thread

class NewsFragment : Fragment() {

    lateinit var binding: FragmentNewsBinding
    val viewModel: NewsViewModel by viewModels()

    companion object {
        fun newInstance() = NewsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNewsBinding.inflate(layoutInflater,container,false).run {
        binding = this
        root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.news.observe(viewLifecycleOwner){

            if (it != null){

                binding.rvRecyclerView.post{

                binding.rvRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    (layoutManager as LinearLayoutManager).setReverseLayout(true)
                    (layoutManager as LinearLayoutManager).setStackFromEnd(true)
                    adapter = NewsAdapter(it)
                }

                view.findViewById<Button>(R.id.btn_news_newPost)?.setOnClickListener() {
                    (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEWS_POST)
                }

                binding.progressBar.visibility = View.GONE
            }

            }
        }

    }

    class NewsViewModel : ViewModel(){

        var news = MutableLiveData<List<Novelty>>()

        init {
            viewModelScope.launch(Dispatchers.IO) {

                newsRepository.loadNewsData(news) { fetchedNews, news ->
                    news.postValue(fetchedNews)
                }

            }
        }

    }

    class NewsViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    class NewsAdapter(val news : List<Novelty>) : RecyclerView.Adapter<NewsViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsViewHolder(
            CardNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

           val novelty = news[position]
            holder.binding.newsTitleNoImage.text= novelty.title
            holder.binding.cardNews.setOnClickListener {

                holder.binding.cardNews.context.startActivity(
                    Intent(
                        holder.binding.cardNews.context,
                        ViewNoveltyActivity::class.java
                    ).apply {
                        putExtra(ViewNoveltyActivity.EXTRA_NOVELTY_ID,novelty.id)
                    }
                )
                //(holder.binding.newsTitleNoImage.context as MainActivity).changeToFragment(TAG_FRAGMENT_NOVELTY)
            }

        }

        override fun getItemCount() = news.size

    }


}



