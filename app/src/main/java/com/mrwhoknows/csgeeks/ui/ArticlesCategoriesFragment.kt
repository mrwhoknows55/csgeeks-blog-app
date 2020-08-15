package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrwhoknows.csgeeks.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.CategoryAdapter
import com.mrwhoknows.csgeeks.adapter.OnItemClickListener
import com.mrwhoknows.csgeeks.model.ArticleTags
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_categories.*

private const val TAG = "ArticlesCategories"

class ArticlesCategoriesFragment : Fragment(R.layout.fragment_categories), OnItemClickListener {

    private lateinit var viewModel: BlogViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        viewModel.getArticleTags()
        viewModel.tags.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    Log.d(TAG, "tags: ${it.data}")
                    it.data?.let { tags ->
                        initRecyclerView(tags)
                    }
                }
            }
        })
    }

    private fun initRecyclerView(tags: ArticleTags) {
        rvCategory.apply {
            layoutManager = LinearLayoutManager(activity)
            categoryAdapter = CategoryAdapter(tags, this@ArticlesCategoriesFragment)
            adapter = categoryAdapter
        }
    }

    override fun onItemClicked(tag: String) {
        Log.d(TAG, "onItemClicked: $tag")
        findNavController().navigate(
            ArticlesCategoriesFragmentDirections.actionArticlesCategoriesFragmentToArticlesByCategoryFragment(
                tag
            )
        )
    }
}