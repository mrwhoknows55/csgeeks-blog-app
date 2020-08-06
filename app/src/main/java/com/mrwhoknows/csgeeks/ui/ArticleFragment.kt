package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.api.RetrofitInstance
import io.noties.markwon.Markwon
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_article.bounceLoader
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private const val TAG = "ArticleFragment"

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var args: ArticleFragmentArgs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = ArticleFragmentArgs.fromBundle(requireArguments())

        val articleID = args.articleID
        isLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val articleResponse = RetrofitInstance.api.getPostById(articleID)
            if (articleResponse.isSuccessful) {
                Log.d(TAG, "onCreate: ${articleResponse.body().toString()}")
                val data = articleResponse.body()!!.article
                withContext(Dispatchers.Main) {
                    isLoading(false)
                    tvArticleTitle.text = data.title
                    val markwon = Markwon.builder(requireContext())
                        .usePlugin(GlideImagesPlugin.create(requireContext()))
                        .build()
                    markwon.setMarkdown(tvArticleBody, data.content)

                    Glide.with(view.context).load(data.thumbnail).into(ivArticleThumbnail)

                    val inputDateFormatter =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00", Locale.getDefault())
                    inputDateFormatter.timeZone = TimeZone.getTimeZone("UTC")
                    val outputDateFormatter =
                        SimpleDateFormat("dd, MMM yyyy hh:mm a", Locale.getDefault())
                    outputDateFormatter.timeZone = TimeZone.getDefault()

                    val dateTime = inputDateFormatter.parse(data.created)
                    val date = outputDateFormatter.format(dateTime!!)

                    tvAuthorName.text = "Created by, ${data.author} at  $date"
                }
            } else {
                Log.d(TAG, "onViewCreated: error")
                withContext(Dispatchers.Main) {
                    isLoading(false)
                    Snackbar.make(
                        view,
                        "Something wrong happened please try later!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isLoading(bool: Boolean) {
        if (bool) {
            bounceLoader.visibility = View.VISIBLE
        } else {
            bounceLoader.visibility = View.GONE
        }
    }
}