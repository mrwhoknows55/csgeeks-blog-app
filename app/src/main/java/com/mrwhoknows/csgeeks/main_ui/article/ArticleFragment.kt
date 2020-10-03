package com.mrwhoknows.csgeeks.main_ui.article

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.View
import android.view.animation.Transformation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ArticleFragment"

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var args: ArticleFragmentArgs
    private lateinit var viewModel: BlogViewModel
    private lateinit var authorName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel = (activity as MainActivity).viewModel
        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(BlogViewModel::class.java)
        args =
            ArticleFragmentArgs.fromBundle(
                requireArguments()
            )

        val articleID = args.articleID

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getArticle(articleID)
        }

        viewModel.article.observe(viewLifecycleOwner, Observer { articleResource ->
            when (articleResource) {
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                    Util.isLoading(bounceLoaderBG, true)
                }
                is Resource.Success -> {
                    articleResource.data?.let {
                        val data = it.article
                        authorName = data.author

                        val date = Util.convertDateTimeToString(
                            data.created,
                            "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00",
                            "dd, MMM yyyy hh:mm a"
                        )

                        val articleHeader =
                            "# ${data.title}\n![thumb](${data.thumbnail})  \n\nCreated by," +
                                " [${data.author}](https://google.com)   \n" + "at $date  \n"

                        val markwon = Markwon.builder(requireContext())
                            .usePlugin(
                                GlideImagesPlugin.create(
                                    (object : GlideImagesPlugin.GlideStore {
                                        override fun load(drawable: AsyncDrawable) =
                                            Glide.with(requireContext())
                                                .load(drawable.destination)
                                                .placeholder(R.drawable.placeholder_horizontal)

                                        override fun cancel(target: Target<*>) =
                                            Glide.with(requireContext()).clear(target)
                                    })
                                )
                            )
                            .build()
                        markwon.setMarkdown(tvArticleBody, articleHeader + "\n"+ data.content)
                    }

                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                }
                is Resource.Error -> {
                    Log.d(TAG, "onViewCreated: error")
                    articleResource.message?.let {
                        Snackbar.make(
                            view,
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    Util.isLoading(bounceLoader, false)
                    Util.isLoading(bounceLoaderBG, false)
                }
            }

        })

        //TODO solve this

        // tvAuthorName.setOnClickListener {
        //     CoroutineScope(Dispatchers.IO).launch {
        //         viewModel.getAuthor(authorName)
        //     }
        //     findNavController().navigate(R.id.action_articleFragment_to_authorFragment)
        // }
    }
}