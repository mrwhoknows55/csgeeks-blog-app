package com.mrwhoknows.csgeeks.ui.home_page.article

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.databinding.FragmentArticleBinding
import com.mrwhoknows.csgeeks.repository.BlogRepositoryImpl
import com.mrwhoknows.csgeeks.util.Constants
import com.mrwhoknows.csgeeks.util.Constants.BLOG_SITE_URL_WITH_HTTPS
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ArticleFragment"

class ArticleFragment : Fragment() {

    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var binding : FragmentArticleBinding
    private lateinit var viewModel: BlogViewModel
    private lateinit var authorName: String
    var articleID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val blogRepository = BlogRepositoryImpl()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[BlogViewModel::class.java]

        articleID = args.articleID

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getArticle(articleID)
        }

        viewModel.article.observe(viewLifecycleOwner) { articleResource ->
            when (articleResource) {
                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
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

                        val authorDeepLink = BLOG_SITE_URL_WITH_HTTPS + "/author.html?name=${data.author}"

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
                        markwon.setMarkdown(binding.tvArticleBody, articleHeader + "\n" + data.content)
                    }

                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
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
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                }
            }

        }
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
                    "Checkout awesome article: ${Constants.BLOG_SITE_URL_WITHOUT_HTTPS}/post.html?id=$articleID"
                )
            }
            startActivity(Intent.createChooser(intent, "Share The Article"))
            return true
        }
        return false
    }
}