package com.mrwhoknows.csgeeks.ui.home_page.article

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Constants
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

    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var viewModel: BlogViewModel
    private lateinit var authorName: String
    var articleID = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(BlogViewModel::class.java)

        articleID = args.articleID

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getArticle(articleID)
        }

        viewModel.article.observe(viewLifecycleOwner, { articleResource ->
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

                        val authorDeepLink =
                            "https://csgeeks-blog.000webhostapp.com/author.html?name=${data.author}"

                        val articleHeader =
                            "# ${data.title}\n![thumb](${data.thumbnail})  \n\nCreated by," +
                                    " [${data.author}]($authorDeepLink)   \n" + "at $date  \n"

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
                        markwon.setMarkdown(tvArticleBody, articleHeader + "\n" + data.content)
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.article_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Checkout awesome article: ${Constants.BLOG_SITE_URL}/post.html?id=$articleID"
                )
            }
            startActivity(Intent.createChooser(intent, "Share The Article"))
            return true
        }
        return false
    }
}