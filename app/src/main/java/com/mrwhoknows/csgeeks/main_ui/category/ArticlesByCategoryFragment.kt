package com.mrwhoknows.csgeeks.main_ui.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.main_ui.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_articles_by_category.*

private const val TAG = "ArticlesByCategory"

class ArticlesByCategoryFragment : Fragment(R.layout.fragment_articles_by_category) {

    private lateinit var viewModel: BlogViewModel
    private lateinit var args: ArticlesByCategoryFragmentArgs
    private lateinit var articleAdapter: ArticleListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        args =
            ArticlesByCategoryFragmentArgs.fromBundle(
                requireArguments()
            )

        viewModel.getArticlesByTag(args.tag)

        viewModel.articles.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }

                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    response.data?.let { articleList ->
                        initRecyclerView(articleList)
                        articleAdapter.setOnItemClickListener {
                            findNavController().navigate(
                                ArticlesByCategoryFragmentDirections.actionArticlesByCategoryFragmentToArticleFragment(
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
                        Snackbar.make(view, "Error: $it", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun initRecyclerView(data: ArticleList) {
        rv_articleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }
}