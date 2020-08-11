package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mrwhoknows.csgeeks.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.fragment_create_article.*
import java.util.concurrent.Executors

private const val TAG = "CreateArticleFragment"

class CreateArticleFragment : Fragment(R.layout.fragment_create_article) {

    lateinit var article: Article.Article
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

        btCreateArticle.setOnClickListener {
            getInput()
        }
    }

    private fun getInput() {
        val title = etArticleTitle.text.toString()
        val authorName = etArticleAuthorName.text.toString()
        val thumbLink = etArticleThumbnailLink.text.toString()
        val tag = etArticleTags.text.toString()
        val tags = tag.split(",")
        val desc = etArticleDescription.text.toString()
        val content = etArticleContent.text.toString()

        article = Article.Article(authorName, content, desc, tags, thumbLink, title)
        viewModel.sendArticleToServer(article)

        viewModel.createArticleResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Toast.makeText(context, "Creating...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    if (it.data!!.success) {
                        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Failed to post", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "fail: ${it.message}")
                }
            }
        })
    }
}