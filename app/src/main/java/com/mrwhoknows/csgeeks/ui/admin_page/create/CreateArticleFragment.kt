package com.mrwhoknows.csgeeks.ui.admin_page.create

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.CreateArticle
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_create_article.*

private const val TAG = "CreateArticleFragment"

class CreateArticleFragment : Fragment(R.layout.fragment_create_article) {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Util.isLoading(bounceLoader, false)
        Util.isLoading(bounceLoaderBG, false)

        val authorName = (requireActivity() as AdminActivity).authorName
        Log.d(TAG, "onCreate: $authorName ")

        if (authorName.isNotEmpty())
            etArticleAuthorName.setText(authorName)

        btEnterBody.setOnClickListener {
            getInput()
        }
    }

    private fun getInput(): Boolean {
        lateinit var article: CreateArticle

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

        article = CreateArticle(authorName, null, desc, tags, thumbLink, title)

        this.findNavController().navigate(
            CreateArticleFragmentDirections.actionCreateArticleFragmentToCreateArticleBodyFragment(
                article
            )
        )
        return true
    }
}