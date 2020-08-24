package com.mrwhoknows.csgeeks.main_ui.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_create_article.*

private const val TAG = "CreateArticleFragment"

class CreateArticleFragment : Fragment(R.layout.fragment_create_article) {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btEnterBody.setOnClickListener {
            getInput()
        }
    }

    private fun getInput(): Boolean {
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
            CreateArticleFragmentDirections.actionCreateArticleFragment2ToCreateArticleBodyFragment(
                title,
                desc,
                authorName,
                thumbLink,
                tags
            )
        )
        return true
    }
}