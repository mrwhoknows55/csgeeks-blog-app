package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.api.RetrofitInstance
import com.mrwhoknows.csgeeks.model.ArticleList
import kotlinx.android.synthetic.main.fragment_articles_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ListFragment"

class ArticlesListFragment : Fragment(R.layout.fragment_articles_list) {

    private lateinit var articleAdapter: ArticleListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btGetAllArticles.setOnClickListener {
            getAllArticles()
        }
    }

    private fun initRecyclerView(data: ArticleList) {
        rv_articleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }

    private fun getAllArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            val postMetaResponse = RetrofitInstance.api.getAllPosts()

            if (postMetaResponse.isSuccessful) {
                Log.d(TAG, "onCreate: ${postMetaResponse.body().toString()}")
                if (postMetaResponse.body()!!.articles.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        initRecyclerView(postMetaResponse.body()!!)
                        articleAdapter.setOnItemClickListener {
                            findNavController().navigate(
                                ArticlesListFragmentDirections.actionArticlesListFragmentToArticleFragment(
                                    it.id.toString()
                                )
                            )
                        }
                    }
                } else
                    Log.d(TAG, "error: ${postMetaResponse.message()}")
            } else
                Log.d(TAG, "error: ${postMetaResponse.message()}")
        }
    }
}