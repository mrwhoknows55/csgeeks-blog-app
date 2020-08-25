package com.mrwhoknows.csgeeks.admin_ui.articles

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.admin_ui.AdminActivity
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_all_articles.*

private const val TAG = "YourArticlesFragment"

class YourArticlesFragment : Fragment(R.layout.fragment_articles_list) {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as AdminActivity).viewModel
        showYourArticles()
    }

    private fun initRecyclerView(data: ArticleList) {
        rv_articleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }

    private fun showYourArticles() {
        viewModel.getAllArticles()
        viewModel.articles.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    response.data?.let { articleList ->
                        initRecyclerView(articleList)
                        articleAdapter.setOnItemClickListener {
                            findNavController().navigate(
                                YourArticlesFragmentDirections.actionYourArticlesToEditArticleFragment(
                                    it.id.toString()
                                )
                            )
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
}