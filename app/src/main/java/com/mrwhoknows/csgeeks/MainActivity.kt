package com.mrwhoknows.csgeeks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)
    }
}