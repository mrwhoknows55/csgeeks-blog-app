package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.ArticleViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ArticleFragment"

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var args: ArticleFragmentArgs
    private lateinit var viewModel: ArticleViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        args = ArticleFragmentArgs.fromBundle(requireArguments())

        val articleID = args.articleID

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getArticle(articleID)
        }

        viewModel.article.observe(viewLifecycleOwner, Observer { articleResource ->
            when (articleResource) {
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                }
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)

                    val resource = articleResource.data?.let {
                        val data = it.article

                        tvArticleTitle.text = data.title
                        tvAuthorName.text = "Created by, ${data.author}"

                        val date = Util.convertDateTimeToString(
                            data.created,
                            "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00",
                            "dd, MMM yyyy hh:mm a"
                        )
                        tvArticleDate.text = "at  $date"

                        val markwon = Markwon.builder(requireContext())
                            .usePlugin(GlideImagesPlugin.create(requireContext()))
                            .build()
                        markwon.setMarkdown(tvArticleBody, data.content)

                        Glide.with(view.context).load(data.thumbnail).into(ivArticleThumbnail)
                    }
                }
                is Resource.Error -> {
                    Log.d(TAG, "onViewCreated: error")
                    Util.isLoading(bounceLoader, false)
                    articleResource.message?.let {
                        Snackbar.make(
                            view,
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })

        // TODO After changes from backend and

        //     tvAuthorName.setOnClickListener {
        //         viewModel.author.observe(viewLifecycleOwner, Observer { authorResource ->
        //             when (authorResource) {
        //                 is Resource.Success -> {
        //                     authorResource.data?.let {
        //                         // Log.d(TAG, "author: ${it.author.id}")
        //                     }
        //                 }
        //             }
        //         })
        //     }
    }
}