package com.mrwhoknows.csgeeks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.model.Author
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

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

    private val _article: MutableLiveData<Resource<Article>> = MutableLiveData()
    val article: LiveData<Resource<Article>> = _article
    private var articleResponse: Article? = null

    suspend fun getArticle(id: String) {
        _article.postValue(Resource.Loading())
        try {
            val response = repository.getArticle(id)
            _article.postValue(handleArticle(response))
        } catch (t: Throwable) {
            if (t is IOException) {
                _article.postValue(Resource.Error("Network Error"))
            } else {
                _article.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleArticle(response: Response<Article>): Resource<Article> {
        if (response.isSuccessful) {
            response.body()?.let {
                articleResponse = it
                return Resource.Success(articleResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    private val _author: MutableLiveData<Resource<Author>> = MutableLiveData()
    val author: LiveData<Resource<Author>> = _author
    private var authorResponse: Author? = null

    suspend fun getAuthor(authorName: String) {
        _author.postValue(Resource.Loading())
        try {
            val response = repository.getAuthor(authorName)
            _author.postValue(handleAuthor(response))
        } catch (t: Throwable) {
            if (t is IOException) _author.postValue(Resource.Error("Network Failure"))
            else _author.postValue(Resource.Error("Conversion Error"))
        }
    }

    private fun handleAuthor(response: Response<Author>): Resource<Author> {
        if (response.isSuccessful) {
            response.body()?.let {
                authorResponse = it
                return Resource.Success(authorResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }
}