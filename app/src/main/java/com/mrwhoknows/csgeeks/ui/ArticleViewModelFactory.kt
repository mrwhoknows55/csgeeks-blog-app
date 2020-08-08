package com.mrwhoknows.csgeeks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mrwhoknows.csgeeks.repository.BlogRepository

class ArticleViewModelFactory(
    private val repository: BlogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticleViewModel(repository) as T
    }
}