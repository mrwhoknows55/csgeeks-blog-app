package com.mrwhoknows.csgeeks.ui.admin_page.articles

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_all_articles.*

private const val TAG = "AllArticlesFragment"

class AllArticlesFragment : Fragment(R.layout.fragment_all_articles) {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as AdminActivity).viewModel
        showAllArticles()
        initCategories()
    }

    private fun initCategories() {
        var tags: List<String>
        viewModel.getArticleTags()
        viewModel.tags.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> {
                    Log.d(TAG, "tags: ${resource.data}")
                    resource.data?.let {
                        tags = it.tags
                        for (tag in tags) {
                            val chip = Chip(chipsCategories.context)
                            chip.text = tag
                            chip.chipBackgroundColor = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.colorBackgroundDark2
                            )

                            chip.isClickable = true
                            chip.isCheckable = true
                            chipsCategories.addView(chip)
                        }
                    }
                    chipsCategories.isSingleSelection = true

                    chipsCategories.setOnCheckedChangeListener { group, id ->
                        val chip = chipsCategories.findViewById<Chip>(id)
                        if (chip != null) {
                            Log.d(TAG, "chip sel: ${chip.text}")
                            viewModel.getArticlesByTag(chip.text.toString())
                        } else {
                            Log.d(TAG, "chip not selected")
                            viewModel.getAllArticles()
                        }
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

    private fun showAllArticles() {
        viewModel.getAllArticles()
        viewModel.articles.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { articleList ->
                        initRecyclerView(articleList)
                        articleAdapter.setOnItemClickListener {
                            try {
                                findNavController().navigate(
                                    AllArticlesFragmentDirections.actionAllArticlesFragmentToArticleFragment2(
                                        it.id.toString()
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                }
                is Resource.Error -> {
                    response.message?.let {
                        Log.e(TAG, "Error: $it")
                        Snackbar.make(requireView(), "Error: $it", Snackbar.LENGTH_SHORT).show()
                    }
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                }
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }
            }
        })
    }
}