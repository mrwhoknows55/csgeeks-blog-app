package com.mrwhoknows.csgeeks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.ui.ArticleViewModel
import com.mrwhoknows.csgeeks.ui.ArticleViewModelFactory

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val blogRepository = BlogRepository()
        val viewModelFactory = ArticleViewModelFactory(blogRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ArticleViewModel::class.java)
    }
}