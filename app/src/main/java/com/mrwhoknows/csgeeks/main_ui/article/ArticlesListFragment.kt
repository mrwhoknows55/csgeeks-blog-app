package com.mrwhoknows.csgeeks.main_ui.article

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import kotlinx.android.synthetic.main.sort_menu_bottom_sheet.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val TAG = "ListFragment"

class ArticlesListFragment : Fragment(R.layout.fragment_articles_list) {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = (activity as MainActivity).viewModel
        initCategories()
        showAllArticles()

        val bottomSheet = sortOptionsBottomSheetLayout
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        btnSort.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            tvApplyBtn.setOnClickListener {
                val id = sortByOptions.checkedRadioButtonId
                val option = requireView().findViewById<RadioButton>(id)
                when (option.tag) {
                    "1" -> viewModel.orderArticlesBy("created", "desc")

                    "2" -> viewModel.orderArticlesBy("created", "asc")

                    "3" -> viewModel.orderArticlesBy("title", "asc")

                    "4" -> viewModel.orderArticlesBy("title", "desc")

                    "5" -> viewModel.orderArticlesBy("author", "asc")

                    "6" -> viewModel.orderArticlesBy("author", "desc")

                    else -> viewModel.getAllArticles()
                }

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }


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
        viewModel.tags.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    Log.d(TAG, "tags: ${resource.data}")
                    resource.data?.let {
                        tags = it.tags
                        for (tag in tags) {
                            val chip = Chip(chipsCategories.context)
                            chip.text = tag
                            chip.chipBackgroundColor = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.colorBackgroundDark3
                            )

                            chip.isClickable = true
                            chip.isCheckable = true
                            chipsCategories.addView(chip)
                        }
                    }
                    chipsCategories.isSingleSelection = true

                    chipsCategories.setOnCheckedChangeListener { _, id ->
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
                is Resource.Error -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                    Snackbar.make(requireView(), "Something Went Wrong", Snackbar.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }
            }
        })
    }

    private fun showAllArticles() {
        viewModel.getAllArticles()
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
                                        ArticlesListFragmentDirections.actionArticlesListFragmentToArticleFragment(
                                            it.id.toString()
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            rv_articleList.adapter = null
                            Snackbar.make(requireView(), "No Articles Found", Snackbar.LENGTH_SHORT)
                                .show()
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

//  TODO ref this for chip and sort query
//
//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        chipsCategories.isSingleSelection = true
//        viewModel.getAllArticles()
//    }
//
//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        chipsCategories.isSelected = false
//        chipsCategories.clearCheck()
//        callFilterRequests(position)
//    }

    //TODO: make livedata changes when we go back from search view
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        var job: Job? = null

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    job?.cancel()
                    job = MainScope().launch {
                        viewModel.searchArticles(query)
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })
    }
}