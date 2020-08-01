package com.mrwhoknows.csgeeks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrwhoknows.csgeeks.adapter.ArticleListAdapter
import com.mrwhoknows.csgeeks.api.RetrofitInstance.Companion.api
import com.mrwhoknows.csgeeks.model.PostMeta
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var articleAdapter: ArticleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btGetAllArticles.setOnClickListener {
            getAllArticles()
        }
    }

    private fun initRecyclerView(data: List<PostMeta.Post>) {
        rv_articleList.apply {
            articleAdapter = ArticleListAdapter(data)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }
    }

    private fun getAllArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            val postMetaResponse = api.getAllPosts()

            if (postMetaResponse.isSuccessful) {
                Log.d(TAG, "onCreate: ${postMetaResponse.body().toString()}")
                if (postMetaResponse.body()!!.posts.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        initRecyclerView(postMetaResponse.body()!!.posts)
                    }
                } else
                    Log.d(TAG, "error: ${postMetaResponse.message()}")
            } else
                Log.d(TAG, "error: ${postMetaResponse.message()}")
        }
    }
}