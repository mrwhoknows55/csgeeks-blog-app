package com.mrwhoknows.csgeeks.ui.admin_page.articles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.LoginInfo
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_manage_articles.*

private const val TAG = "YourArticlesFragment"

class YourArticlesFragment : Fragment() {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel
    private var authorName: String = ""
    private var loginToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_manage_articles, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginInfo = LoginInfo(requireActivity())
        loginInfo.authorName?.let {
            authorName = it
        }
        loginInfo.loginToken?.let {
            loginToken = it
        }

        viewModel = (requireActivity() as AdminActivity).viewModel

        showYourArticles()
        Snackbar.make(
            requireView(),
            "Slide article right to delete.",
            Snackbar.LENGTH_SHORT
        )
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorNordBlue))
            .show()

        swipeToDeleteArticle()
    }

    private fun initRecyclerView(data: ArticleList) {
        rv_articleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }

    private fun showYourArticles() {
        viewModel.getArticlesByAuthor(authorName)
        Log.d(TAG, "showYourArticles: $authorName")
        viewModel.articles.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    response.data?.let { articleList ->
                        if (!articleList.articles.isNullOrEmpty()) {
                            initRecyclerView(articleList)
                            articleAdapter.setOnItemClickListener {
                                try {
                                    findNavController().navigate(
                                        YourArticlesFragmentDirections.actionYourArticlesToEditArticleFragment(
                                            it.id.toString()
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            findNavController().navigate(R.id.action_yourArticles_to_allArticlesFragment)
                            Snackbar.make(
                                requireView(),
                                "Your Articles Not Found",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                is Resource.Error -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    response.message?.let {
                        Log.e(TAG, "Error: $it")
                        Snackbar.make(requireView(), "Error: $it", Snackbar.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }
            }
        })
    }

    private fun swipeToDeleteArticle() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,  // no dragging enabled
            ItemTouchHelper.RIGHT           // swiping only to right
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val article = articleAdapter.articleMetaList.articles[pos]

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Do you want to delete article?")
                    .setMessage("Are you sure?")
                    .setBackground(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.color.colorBackgroundDark3
                        )
                    )
                    .setNegativeButton("No") { _, _ ->
                        articleAdapter.notifyDataSetChanged()
                    }
                    .setPositiveButton("Yes") { _, _ ->
                        deleteArticle(article)
                    }
                    .show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rv_articleList)
        }
    }

    private fun deleteArticle(article: ArticleList.Article) {
        viewModel.deleteArticleToServer(article.id.toString(), loginToken)
        viewModel.deleteArticleResponse.observe(viewLifecycleOwner, { selectedArticle ->
            when (selectedArticle) {
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                }
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }
                is Resource.Error -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    selectedArticle.message?.let {
                        Log.e(TAG, "Error: $it")
                        Snackbar.make(requireView(), "Error: $it", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}