package com.mrwhoknows.csgeeks.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mrwhoknows.csgeeks.repository.BlogRepository

class BlogViewModelFactory(
    private val repository: BlogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BlogViewModel(repository) as T
    }
}