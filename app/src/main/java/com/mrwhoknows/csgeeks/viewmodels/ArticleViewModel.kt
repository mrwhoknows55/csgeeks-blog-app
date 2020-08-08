package com.mrwhoknows.csgeeks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ArticleViewModel(
    private val repository: BlogRepository
) : ViewModel() {

    private val _articles: MutableLiveData<Resource<ArticleList>> = MutableLiveData()
    val articles: LiveData<Resource<ArticleList>> = _articles
    private var articlesResponse: ArticleList? = null

    init {
        viewModelScope.launch {
            getArticles()
        }
    }

    private suspend fun getArticles() {
        _articles.postValue(Resource.Loading())
        try {
            val response = repository.getAllArticles()
            _articles.postValue(handleArticles(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _articles.postValue(Resource.Error("Network Failure"))
                else -> _articles.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleArticles(response: Response<ArticleList>): Resource<ArticleList> {
        if (response.isSuccessful) {
            response.body()?.let {
                articlesResponse = it
                return Resource.Success(articlesResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }
}