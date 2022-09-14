package com.mrwhoknows.csgeeks.ui.admin_page.articles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.databinding.FragmentArticlesListBinding
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel

private const val TAG = "AllArticlesFragment"

class AllArticlesFragment : Fragment() {

    private lateinit var articleAdapter: ArticleListAdapter
    private lateinit var viewModel: BlogViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding : FragmentArticlesListBinding
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

        viewModel = (requireActivity() as AdminActivity).viewModel
        showAllArticles()
        blogTags()
        sortSheet()
    }

    private fun sortSheet() {
        val bottomSheet = requireView().findViewById<ConstraintLayout>(R.id.sortOptionsBottomSheetLayout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.btnSort.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            requireView().findViewById<TextView>(R.id.tvApplyBtn).setOnClickListener {
                val id = requireView().findViewById<RadioGroup>(R.id.sortByOptions).checkedRadioButtonId
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

    private fun initRecyclerView(data: ArticleList) {
        binding.rvArticleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }

    private fun showAllArticles() {
        viewModel.getAllArticles()
        viewModel.articles.observe(viewLifecycleOwner) { response ->
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
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                }
                is Resource.Error -> {
                    response.message?.let {
                        Log.e(TAG, "Error: $it")
                        Snackbar.make(requireView(), "Error: $it", Snackbar.LENGTH_SHORT).show()
                    }
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                }
                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
                }
            }
        }
    }
}