package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.fragment_create_article.*
import java.util.concurrent.Executors

class CreateArticleFragment : Fragment(R.layout.fragment_create_article) {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        val markwon: Markwon = Markwon.create(requireContext())
        val editor: MarkwonEditor = MarkwonEditor.create(markwon)

        etArticleContent.addTextChangedListener(
            MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                etArticleContent
            )
        )

        Util.isLoading(bounceLoader, false)
        Util.isLoading(bounceLoaderBG, false)
        btCreateArticle.setOnClickListener {
            if (getInput())
                sendArticle()
        }
    }

    private fun getInput(): Boolean {
        val title = etArticleTitle.text.toString()
        val authorName = etArticleAuthorName.text.toString()
        val thumbLink = etArticleThumbnailLink.text.toString()
        val tags = etArticleTags.text.toString()
        val desc = etArticleDescription.text.toString()
        val content = etArticleContent.text.toString()

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
        if (content.isEmpty() || content.isBlank()) {
            etArticleContent.error = "Required"
            return false
        }
        article = SendArticle(authorName, content, desc, tags, thumbLink, title)
        return true
    }

    private fun sendArticle() {
        viewModel.sendArticleToServer(article)

        viewModel.createArticleResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                }
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)

                    if (it.data!!.success) {
                        Snackbar.make(
                            requireView(),
                            "Post Created Successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        viewModel.getAllArticles()
                        findNavController().navigate(R.id.action_createArticleFragment_to_articlesListFragment)
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Please Try Again",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Error -> {
                    Util.isLoading(bounceLoaderBG, false)
                    Util.isLoading(bounceLoaderBG, false)
                    Snackbar.make(
                        requireView(),
                        "Server Error: ${it.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}