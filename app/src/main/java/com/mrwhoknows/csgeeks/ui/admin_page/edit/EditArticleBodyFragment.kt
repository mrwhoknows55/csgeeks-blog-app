package com.mrwhoknows.csgeeks.ui.admin_page.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
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

class EditArticleBodyFragment : Fragment() {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel
    private lateinit var args: EditArticleBodyFragmentArgs
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
        args = EditArticleBodyFragmentArgs.fromBundle(requireArguments())

        val markwon: Markwon = Markwon.create(requireContext())
        val editor: MarkwonEditor = MarkwonEditor.create(markwon)


        binding.etArticleContent.addTextChangedListener(
            MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                binding.etArticleContent
            )
        )

        binding.etArticleContent.setText(args.body)

        binding.btCreateArticle.setOnClickListener {
            getInput()
        }
    }

    private fun getInput() {
        val content = binding.etArticleContent.text.toString()

        if (content.isBlank()) {
            binding.etArticleContent.error = "Please enter the content"
        } else {

            article = SendArticle(
                args.authorName,
                content,
                args.description,
                args.tags,
                args.thumbImgLink,
                args.title
            )

            updateArticle()
        }
    }

    private fun updateArticle() {
        viewModel.updateArticleToServer(args.id, article, loginToken)

        viewModel.updateArticleResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
                }
                is Resource.Success -> {

                    if (it.data!!.success) {
                        Snackbar.make(
                            requireView(),
                            "Article Updated Successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        viewModel.getAllArticles()
                        findNavController().navigate(R.id.action_editArticleBodyFragment_to_yourArticles)
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Please Try Again",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                }
                is Resource.Error -> {

                    Snackbar.make(
                        requireView(),
                        "Server Error: ${it.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    Util.isLoading(binding.bounceLoaderBG, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                }
            }
        }
    }
}