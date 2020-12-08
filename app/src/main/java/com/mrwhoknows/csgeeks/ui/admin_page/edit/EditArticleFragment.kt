package com.mrwhoknows.csgeeks.ui.admin_page.edit

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import kotlinx.android.synthetic.main.fragment_create_article.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "CreateArticleFragment"

class EditArticleFragment : Fragment(R.layout.fragment_create_article) {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel
    private lateinit var args: EditArticleFragmentArgs
    var body: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = EditArticleFragmentArgs.fromBundle(requireArguments())
        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)

        btEnterBody.isEnabled = false
        getOldArticleData()
        setOldDataToViews()
    }

    private fun setOldDataToViews() {
        viewModel.article.observe(viewLifecycleOwner, { articleResource ->
            when (articleResource) {

                is Resource.Loading -> {
                    Util.isLoading(bounceLoaderBG, true)
                    Util.isLoading(bounceLoader, true)
                }

                is Resource.Success -> {

                    articleResource.data?.let {
                        val article = it.article
                        //TODO change this to comma method
                        etArticleTags.setText(article.tags[0])
                        etArticleThumbnailLink.setText(article.thumbnail)
                        etArticleTitle.setText(article.title)
                        etArticleAuthorName.setText(article.author)
                        etArticleDescription.setText(article.description)
                        body = article.content

                        btEnterBody.isEnabled = true
                        btEnterBody.setOnClickListener {
                            getInput()
                        }
                    }
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
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

                    Util.isLoading(bounceLoaderBG, false)
                    Util.isLoading(bounceLoader, false)
                }
            }
        })
    }

    private fun getOldArticleData() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getArticle(args.articleID)
        }
    }

    private fun getInput()
        : Boolean {
        val title = etArticleTitle.text.toString()
        val authorName = etArticleAuthorName.text.toString()
        val thumbLink = etArticleThumbnailLink.text.toString()
        val tags = etArticleTags.text.toString()
        val desc = etArticleDescription.text.toString()

        if (title.isEmpty() || title.isBlank()) {
            etArticleTitle.error = "Required"
            return false
        }
        if (authorName.isEmpty() || authorName.isBlank()) {
            etArticleAuthorName.error = "Required"
            return false
        }
        if (thumbLink.isEmpty() || thumbLink.isBlank()) {
            etArticleThumbnailLink.error = "Required"
            return false
        }
        if (tags.isEmpty() || tags.isBlank()) {
            etArticleTags.error = "Required"
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