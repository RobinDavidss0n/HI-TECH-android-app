package se.ju.student.hitech

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
        var loggedIn: Boolean = false
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

        if (userRepository.checkIfLoggedIn()) {
            loggedIn = true
            binding.fabCreateNewPost.visibility = VISIBLE
        } else {
            binding.fabCreateNewPost.visibility = GONE
            loggedIn = false
        }

        viewModel.news.observe(viewLifecycleOwner) {

            if (it != null) {

                binding.rvRecyclerView.post {

                    binding.rvRecyclerView.apply {
                        layoutManager = LinearLayoutManager(context)
                        (layoutManager as LinearLayoutManager).reverseLayout = true
                        (layoutManager as LinearLayoutManager).stackFromEnd = true
                        adapter = NewsAdapter(it)

                        registerForContextMenu(this)
                    }
                    binding.swipeRefreshNews.setOnRefreshListener {
                        newsRepository.updateNewsList()
                        binding.swipeRefreshNews.isRefreshing = false
                    }
                    binding.fabCreateNewPost.setOnClickListener {
                        (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEWS_POST)
                    }
                    binding.progressBar.visibility = View.GONE
                }

            }
        }

    }

    class NewsViewModel : ViewModel() {

        var news = newsRepository.news

        init {
            viewModelScope.launch(Dispatchers.IO) {

                newsRepository.loadNewsData(news) { fetchedNews, news ->
                    news.postValue(fetchedNews)
                }

            }
        }

    }

    class NewsViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    class NewsAdapter(val news: List<Novelty>) : RecyclerView.Adapter<NewsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsViewHolder(
            CardNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

            val novelty = news[position]
            holder.binding.newsTitleNoImage.text = novelty.title
            holder.binding.cardNews.setOnClickListener {

                holder.binding.cardNews.context.startActivity(
                    Intent(
                        holder.binding.cardNews.context,
                        ViewNoveltyActivity::class.java
                    ).apply {
                        putExtra(EXTRA_NOVELTY_ID, novelty.id)
                    }
                )
            }

            val id = novelty.id

            if (loggedIn) {
                holder.binding.icMenu.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, holder.binding.icMenu)
                    popupMenu.inflate(R.menu.recyclerview_menu)

                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_delete -> {
                                AlertDialog.Builder(holder.itemView.context)
                                    .setTitle("Delete post")
                                    .setMessage("Do you really want to delete this post?")
                                    .setPositiveButton(
                                        "YES"
                                    ) { dialog, whichButton ->
                                        // delete event
                                        newsRepository.deleteNovelty(id)
                                    }.setNegativeButton(
                                        "NO"
                                    ) { dialog, whichButton ->
                                        // Do not delete
                                    }.show()
                            }
                            R.id.menu_edit -> {
                                //newsRepository.updateNovelty()
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
        override fun getItemCount() = news.size
    }
}



