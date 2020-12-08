package com.mrwhoknows.csgeeks.ui.admin_page.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.LoginInfo
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.fragment_create_article_body.*
import java.util.concurrent.Executors

class CreateArticleBodyFragment : Fragment(R.layout.fragment_create_article_body) {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel
    private lateinit var args: CreateArticleBodyFragmentArgs
    private var loginToken: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Util.isLoading(bounceLoader, false)
        Util.isLoading(bounceLoaderBG, false)

        val loginInfo = LoginInfo(requireActivity())
        loginToken = loginInfo.loginToken

        //TODO MAKE this better

        // viewModel = (activity as MainActivity).viewModel

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)

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
        val content = etArticleContent.text.toString()

        if (content.isBlank()) {
            etArticleContent.error = "Please enter the content"
        } else {
            args = CreateArticleBodyFragmentArgs.fromBundle(requireArguments())

            val articleToSend = args.articleToCreate

            article = SendArticle(
                articleToSend.author!!,
                content,
                articleToSend.description!!,
                articleToSend.tags!!,
                articleToSend.thumbnail!!,
                articleToSend.title!!
            )
            sendArticle()
        }
    }

    private fun sendArticle() {
        viewModel.sendArticleToServer(article,loginToken)

        viewModel.createArticleResponseLiveData.observe(viewLifecycleOwner, {
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
                            "Article Created Successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        viewModel.getAllArticles()
                        findNavController().navigate(R.id.action_createArticleBodyFragment_to_allArticlesFragment)
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