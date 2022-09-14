package com.mrwhoknows.csgeeks.ui.admin_page.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.databinding.FragmentCreateArticleBinding
import com.mrwhoknows.csgeeks.databinding.FragmentCreateArticleBodyBinding
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.LoginInfo
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

class CreateArticleBodyFragment : Fragment() {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel
    private lateinit var args: CreateArticleBodyFragmentArgs
    private lateinit var binding: FragmentCreateArticleBodyBinding
    private var loginToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateArticleBodyBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Util.isLoading(binding.bounceLoader, false)
        Util.isLoading(binding.bounceLoaderBG, false)

        LoginInfo(requireActivity()).loginToken?.let {
            loginToken = it
        }

        viewModel = (requireActivity() as AdminActivity).viewModel

        val markwon: Markwon = Markwon.create(requireContext())
        val editor: MarkwonEditor = MarkwonEditor.create(markwon)
        binding.etArticleContent.addTextChangedListener(
            MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                binding.etArticleContent
            )
        )

        binding.btCreateArticle.setOnClickListener {
            getInput()
        }
    }

    private fun getInput() {
        val content = binding.etArticleContent.text.toString()

        if (content.isBlank()) {
            binding.etArticleContent.error = "Please enter the content"
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
        viewModel.sendArticleToServer(article, loginToken)

        viewModel.createArticleResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                }
                is Resource.Success -> {
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)

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
                    Util.isLoading(binding.bounceLoaderBG, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                    Snackbar.make(
                        requireView(),
                        "Server Error: ${it.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}