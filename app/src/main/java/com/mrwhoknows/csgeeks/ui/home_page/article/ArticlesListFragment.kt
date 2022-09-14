package com.mrwhoknows.csgeeks.ui.home_page.article

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.databinding.FragmentArticlesListBinding
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.ui.home_page.MainActivity
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val TAG = "ListFragment"

class ArticlesListFragment : Fragment() {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: FragmentArticlesListBinding
    private var selectedTag: String = ""
    private var sortBy: String = ""
    private var order: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = (requireActivity() as MainActivity).viewModel
        blogTags()
        showAllArticles()
        sortSheet()
    }

    private fun sortSheet() {
        val bottomSheet =
            requireView().findViewById<ConstraintLayout>(R.id.sortOptionsBottomSheetLayout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.btnSort.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheet.findViewById<TextView>(R.id.tvApplyBtn).setOnClickListener {
                val id =
                    bottomSheet.findViewById<RadioGroup>(R.id.sortByOptions).checkedRadioButtonId
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

    private fun initRecyclerView(data: ArticleList) {
        binding.rvArticleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }

    private fun blogTags() {
        var tags: List<String>
        viewModel.getArticleTags()
        viewModel.tags.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                    Log.d(TAG, "tags: ${resource.data}")
                    resource.data?.let {
                        tags = it.tags
                        for (tag in tags) {
                            val chip = Chip(binding.chipsCategories.context)
                            chip.text = tag
                            chip.chipBackgroundColor = ContextCompat.getColorStateList(
                                requireContext(),
                                R.color.colorBackgroundDark3
                            )

                            chip.isClickable = true
                            chip.isCheckable = true
                            binding.chipsCategories.addView(chip)
                        }
                    }
                    binding.chipsCategories.isSingleSelection = true

                    binding.chipsCategories.setOnCheckedChangeListener { _, id ->
                        val chip = binding.chipsCategories.findViewById<Chip>(id)
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
                            selectedTag = ""
                            Log.d(TAG, "chip not selected")
                            if (sortBy.isNotEmpty() and order.isNotEmpty())
                                viewModel.orderArticlesBy(sortBy, order)
                            else
                                viewModel.getAllArticles()
                        }
                    }
                }
                is Resource.Error -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
                    Snackbar.make(requireView(), "Something Went Wrong", Snackbar.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
                }
            }
        }
    }

    private fun showAllArticles() {
        viewModel.getAllArticles()
        viewModel.articles.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
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
                            binding.rvArticleList.adapter = null
                            Snackbar.make(
                                requireView(),
                                "No Articles Found",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("No Articles Found")
                                .setMessage("Do you want to retry?")
                                .setPositiveButton("Yes, Retry") { dialog, _ ->
                                    showAllArticles()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("No, Exit") { dialog, _ ->
                                    dialog.dismiss()
                                    requireActivity().finish()
                                }

                        }
                    }
                }
                is Resource.Error -> {
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                    response.message?.let {
                        Log.e(TAG, "Error: $it")
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Error: $it")
                            .setMessage("Do you want to retry?")
                            .setPositiveButton("Yes, Retry") { dialog, _ ->
                                showAllArticles()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No, Exit") { dialog, _ ->
                                dialog.dismiss()
                                requireActivity().finish()
                            }
                    }
                }
                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
                }
            }
        }
    }

    //TODO: call showAllArticles() changes when we go back from search view
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        var job: Job? = null

        searchView.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    job?.cancel()
                    job = MainScope().launch {
                        viewModel.searchArticles(query)
                    }
                }
                searchView.clearFocus()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }
        })
    }
}