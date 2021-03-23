package se.ju.student.hitech.news

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.ju.student.hitech.MainActivity
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_CREATE_NEWS
import se.ju.student.hitech.MainActivity.Companion.TAG_FRAGMENT_UPDATE_NEWS
import se.ju.student.hitech.R
import se.ju.student.hitech.databinding.CardNewsBinding
import se.ju.student.hitech.databinding.FragmentNewsBinding
import se.ju.student.hitech.dialogs.DeleteNewsAlertDialog
import se.ju.student.hitech.dialogs.DeleteNewsAlertDialog.Companion.TAG_DELETE_NEWS_DIALOG
import se.ju.student.hitech.news.NewsRepository.Companion.newsRepository
import se.ju.student.hitech.news.ViewNewsActivity.Companion.EXTRA_NEWS_ID
import se.ju.student.hitech.user.UserRepository.Companion.userRepository

class NewsFragment : Fragment() {

    lateinit var binding: FragmentNewsBinding
    private val viewModel: NewsViewModel by viewModels()

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

        binding.rvRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            registerForContextMenu(this)
        }

        val manager = parentFragmentManager

        viewModel.news.observe(viewLifecycleOwner) {

            if (it != null) {
                binding.rvRecyclerView.post {
                    binding.rvRecyclerView.apply {
                        adapter = NewsAdapter(it, manager)
                        adapter?.notifyDataSetChanged()
                    }
                    binding.progressBar.visibility = GONE
                }
            }
        }

        loggedIn = userRepository.checkIfLoggedIn()

        if (loggedIn) {
            binding.fabCreateNewPost.visibility = VISIBLE
        } else {
            binding.fabCreateNewPost.visibility = GONE
        }

        binding.fabCreateNewPost.setOnClickListener {
            (context as MainActivity).changeToFragment(TAG_FRAGMENT_CREATE_NEWS)
        }
    }

    class NewsViewModel : ViewModel() {
        var news = MutableLiveData<List<News>>()

        init {
            newsRepository.listenForNewsChanges { result, list ->
                when (result) {
                    "successful" -> {
                        news.postValue(list.asReversed())

                    }
                    "internalError" -> {
                        val errorList = mutableListOf<News>()
                        val error = News()
                        //Can't use getString() in ViewModel so that's why it's hard coded
                        error.title =
                            "Error getting news, check your internet connection and restart the app."
                        errorList.add(error)
                        news.postValue(errorList)
                    }
                }
            }
        }
    }

    class NewsViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    class NewsAdapter(val news: List<News>, private val manager: FragmentManager?) :
        RecyclerView.Adapter<NewsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsViewHolder(
            CardNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            val newsPost = news[position]
            val id = newsPost.id

            holder.binding.textviewNewsTitle.text = newsPost.title
            holder.binding.cardNews.setOnClickListener {

                holder.binding.cardNews.context.startActivity(
                    Intent(
                        holder.binding.cardNews.context,
                        ViewNewsActivity::class.java
                    ).apply {
                        putExtra(EXTRA_NEWS_ID, id)
                    }
                )
            }

            if (loggedIn) {
                holder.binding.icMenu.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, holder.binding.icMenu)
                    popupMenu.inflate(R.menu.recyclerview_menu)

                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_delete -> {
                                if (manager != null) {
                                    val deleteNewsDialog = DeleteNewsAlertDialog()
                                    // pass the clicked id to the DialogFragment
                                    var argument = Bundle()
                                    argument.putInt("news_id", id)
                                    deleteNewsDialog.arguments = argument
                                    deleteNewsDialog.show(
                                        manager,
                                        TAG_DELETE_NEWS_DIALOG
                                    )
                                }
                            }
                            R.id.menu_edit -> {
                                (holder.itemView.context as MainActivity).setClickedNewsId(id)
                                (holder.itemView.context as MainActivity).changeToFragment(
                                    TAG_FRAGMENT_UPDATE_NEWS
                                )
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
