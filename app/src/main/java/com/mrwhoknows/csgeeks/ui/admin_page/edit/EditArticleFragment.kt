package com.mrwhoknows.csgeeks.ui.admin_page.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.databinding.FragmentCreateArticleBinding
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.repository.BlogRepositoryImpl
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CreateArticleFragment"

class EditArticleFragment : Fragment() {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel
    private lateinit var args: EditArticleFragmentArgs
    private lateinit var binding: FragmentCreateArticleBinding
    var body: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = EditArticleFragmentArgs.fromBundle(requireArguments())
        val blogRepository = BlogRepositoryImpl()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)

        binding.btEnterBody.isEnabled = false
        getOldArticleData()
        setOldDataToViews()
    }

    private fun setOldDataToViews() {
        viewModel.article.observe(viewLifecycleOwner) { articleResource ->
            when (articleResource) {

                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoaderBG, true)
                    Util.isLoading(binding.bounceLoader, true)
                }

                is Resource.Success -> {
                    articleResource.data?.let { it ->
                        val article = it.article

                        var tags = ""
                        article.tags.forEachIndexed { pos, tag ->
                            tags += if (pos == 0)
                                tag
                            else
                                ",$tag"
                        }
                        binding.etArticleTags.setText(tags)
                        binding.etArticleThumbnailLink.setText(article.thumbnail)
                        binding.etArticleTitle.setText(article.title)
                        binding.etArticleAuthorName.setText(article.author)
                        binding.etArticleDescription.setText(article.description)
                        body = article.content

                        binding.btEnterBody.isEnabled = true
                        binding.btEnterBody.setOnClickListener {
                            getInput()
                        }
                    }
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                }

                is Resource.Error -> {

                    Log.d(TAG, "onViewCreated: error")
                    articleResource.message?.let {
                        Snackbar.make(
                            requireView(),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    Util.isLoading(binding.bounceLoaderBG, false)
                    Util.isLoading(binding.bounceLoader, false)
                }
            }
        }
    }

    private fun getOldArticleData() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getArticle(args.articleID)
        }
    }

    private fun getInput()
            : Boolean {
        val title = binding.etArticleTitle.text.toString()
        val authorName = binding.etArticleAuthorName.text.toString()
        val thumbLink = binding.etArticleThumbnailLink.text.toString()
        val tags = binding.etArticleTags.text.toString()
        val desc = binding.etArticleDescription.text.toString()

        if (title.isEmpty() || title.isBlank()) {
            binding.etArticleTitle.error = "Required"
            return false
        }
        if (authorName.isEmpty() || authorName.isBlank()) {
            binding.etArticleAuthorName.error = "Required"
            return false
        }
        if (thumbLink.isEmpty() || thumbLink.isBlank()) {
            binding.etArticleThumbnailLink.error = "Required"
            return false
        }
        if (tags.isEmpty() || tags.isBlank()) {
            binding.etArticleTags.error = "Required"
            return false
        }

        // TODO CHANGE this safe args to serializable  or parcelable
        findNavController().navigate(
            EditArticleFragmentDirections.actionEditArticleFragmentToEditArticleBodyFragment(
                title,
                desc,
                authorName,
                thumbLink,
                tags,
                body,
                args.articleID
            )
        )
        return true
    }
}