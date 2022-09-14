package com.mrwhoknows.csgeeks.ui.admin_page.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mrwhoknows.csgeeks.databinding.FragmentCreateArticleBinding
import com.mrwhoknows.csgeeks.model.CreateArticle
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel

private const val TAG = "CreateArticleFragment"

class CreateArticleFragment : Fragment() {

    lateinit var article: SendArticle
    lateinit var viewModel: BlogViewModel
    private lateinit var binding: FragmentCreateArticleBinding

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
        Util.isLoading(binding.bounceLoader, false)
        Util.isLoading(binding.bounceLoaderBG, false)

        val authorName = (requireActivity() as AdminActivity).authorName
        Log.d(TAG, "onCreate: $authorName ")

        if (authorName.isNotEmpty())
            binding.etArticleAuthorName.setText(authorName)

        binding.btEnterBody.setOnClickListener {
            getInput()
        }
    }

    private fun getInput(): Boolean {
        lateinit var article: CreateArticle

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

        article = CreateArticle(authorName, null, desc, tags, thumbLink, title)

        this.findNavController().navigate(
            CreateArticleFragmentDirections.actionCreateArticleFragmentToCreateArticleBodyFragment(
                article
            )
        )
        return true
    }
}