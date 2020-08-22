package com.mrwhoknows.csgeeks.main_ui.article

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.main_ui.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.android.synthetic.main.fragment_articles_list.bounceLoader
import kotlinx.android.synthetic.main.fragment_articles_list.bounceLoaderBG

private const val TAG = "ListFragment"

class ArticlesListFragment : Fragment(R.layout.fragment_articles_list) {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        initCategories()

        val sharedPreferences = requireActivity().getSharedPreferences("TOKEN", 0)
        if (sharedPreferences.getBoolean("IS_LOGGED_IN", false)) {
            //author or admin user
            val token = sharedPreferences.getString("LOGIN_TOKEN", "empty")
            viewModel.isLoggedUserLoggedIn(token!!)
            viewModel.isLoggedIn.observe(viewLifecycleOwner, Observer {
                if (it is Resource.Success) {
                    if (it.data!!.success) {
                        fabCreateArticle.visibility = View.VISIBLE
                        fabCreateArticle.setOnClickListener {
                            findNavController().navigate(R.id.action_articlesListFragment_to_createArticleFragment)
                        }
                    }
                }
                if (it is Resource.Error)
                    return@Observer
            })
        } else {
            //regular user
            fabCreateArticle.visibility = View.GONE
        }

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
                                ArticlesListFragmentDirections.actionArticlesListFragmentToArticleFragment(
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
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
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

    private fun initCategories() {
        var tags: List<String>
        viewModel.getArticleTags()
        viewModel.tags.observe(viewLifecycleOwner, Observer { resource ->
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
}