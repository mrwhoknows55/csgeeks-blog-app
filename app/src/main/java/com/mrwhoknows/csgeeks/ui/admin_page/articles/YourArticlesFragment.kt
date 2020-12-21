package com.mrwhoknows.csgeeks.ui.admin_page.articles

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.LoginInfo
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.android.synthetic.main.sort_menu_bottom_sheet.*

private const val TAG = "YourArticlesFragment"

class YourArticlesFragment : Fragment(R.layout.fragment_articles_list) {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel
    private var authorName: String = ""
    private var loginToken: String = ""
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var selectedTag: String = ""
    private var sortBy: String = ""
    private var order: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginInfo = LoginInfo(requireActivity())
        loginInfo.authorName?.let {
            authorName = it
        }
        loginInfo.loginToken?.let {
            loginToken = it
        }

        viewModel = (activity as AdminActivity).viewModel

        showYourArticles()
        swipeToDeleteArticle()
        blogTags()
        sortSheet()
    }

    private fun sortSheet() {
        val bottomSheet = sortOptionsBottomSheetLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        btnSort.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            tvApplyBtn.setOnClickListener {
                val id = sortByOptions.checkedRadioButtonId
                val option = requireView().findViewById<RadioButton>(id)

                if (selectedTag.isEmpty()) {
                    when (option.tag) {
                        "1" -> {
                            sortBy = "created"
                            order = "desc"
                            viewModel.orderArticlesBy(sortBy, order)
                        }
                        "2" -> {
                            sortBy = "created"
                            order = "asc"
                            viewModel.orderArticlesBy(sortBy, order)
                        }
                        "3" -> {
                            sortBy = "title"
                            order = "asc"
                            viewModel.orderArticlesBy(sortBy, order)
                        }
                        "4" -> {
                            sortBy = "title"
                            order = "desc"
                            viewModel.orderArticlesBy(sortBy, order)
                        }
                        "5" -> {
                            sortBy = "author"
                            order = "asc"
                            viewModel.orderArticlesBy(sortBy, order)
                        }
                        "6" -> {
                            sortBy = "author"
                            order = "desc"
                            viewModel.orderArticlesBy(sortBy, order)
                        }

                        else -> viewModel.getAllArticles()
                    }
                } else {
                    when (option.tag) {
                        "1" -> {
                            sortBy = "created"
                            order = "desc"
                            viewModel.orderArticlesBy(selectedTag, sortBy, order)
                        }
                        "2" -> {
                            sortBy = "created"
                            order = "asc"
                            viewModel.orderArticlesBy(selectedTag, sortBy, order)
                        }
                        "3" -> {
                            sortBy = "title"
                            order = "asc"
                            viewModel.orderArticlesBy(selectedTag, sortBy, order)
                        }
                        "4" -> {
                            sortBy = "title"
                            order = "desc"
                            viewModel.orderArticlesBy(selectedTag, sortBy, order)
                        }
                        "5" -> {
                            sortBy = "author"
                            order = "asc"
                            viewModel.orderArticlesBy(selectedTag, sortBy, order)
                        }
                        "6" -> {
                            sortBy = "author"
                            order = "desc"
                            viewModel.orderArticlesBy(selectedTag, sortBy, order)
                        }

                        else -> viewModel.getAllArticles()
                    }
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }


    private fun blogTags() {
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
                            selectedTag = chip.text.toString()
                            if (sortBy.isNotEmpty() and order.isNotEmpty())
                                viewModel.orderArticlesBy(selectedTag, sortBy, order)
                            else {
                                sortBy = ""
                                viewModel.getAllArticles()
                            }
                        } else {
                            Log.d(TAG, "chip not selected")
                            if (sortBy.isNotEmpty() and order.isNotEmpty())
                                viewModel.orderArticlesBy(sortBy, order)
                            else {
                                selectedTag = ""
                                viewModel.getAllArticles()
                            }
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
            0,
            ItemTouchHelper.RIGHT
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
                Snackbar.make(requireView(), "Are you sure?", Snackbar.LENGTH_LONG).apply {
                    setAction("YES") {
                        deleteArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rv_articleList)
        }
    }

    private fun deleteArticle(article: ArticleList.Article) {
        viewModel.deleteArticleToServer(article.id.toString(), loginToken)
        viewModel.deleteArticleResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    showYourArticles()
                }
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }
                is Resource.Error -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                    it.message?.let {
                        Log.e(TAG, "Error: $it")
                        Snackbar.make(requireView(), "Error: $it", Snackbar.LENGTH_SHORT).show()
                    }
                    showYourArticles()
                }
            }
        })
    }
}