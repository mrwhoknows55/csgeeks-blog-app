package com.mrwhoknows.csgeeks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.model.ArticleTags
import com.mrwhoknows.csgeeks.model.Author
import com.mrwhoknows.csgeeks.model.LoginResponse
import com.mrwhoknows.csgeeks.model.ResultResponse
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class BlogViewModel(
    private val repository: BlogRepository
) : ViewModel() {

    private val _articles: MutableLiveData<Resource<ArticleList>> = MutableLiveData()
    val articles: LiveData<Resource<ArticleList>> = _articles
    private var articlesResponse: ArticleList? = null

    fun getAllArticles() =
        viewModelScope.launch {
            getArticles()
        }

    fun getArticleTags() = viewModelScope.launch {
        getTags()
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

    private val _createArticleResponse: MutableLiveData<Resource<ResultResponse>> =
        MutableLiveData()
    val createArticleResponseLiveData: LiveData<Resource<ResultResponse>> = _createArticleResponse
    private var createArticleResponse: ResultResponse? = null

    fun sendArticleToServer(article: SendArticle) {
        viewModelScope.launch {
            createArticle(article)
        }
    }

    private suspend fun createArticle(article: SendArticle) {
        _createArticleResponse.postValue(Resource.Loading())
        try {
            val response = repository.createArticle(article)
            if (response.isSuccessful) {
                if (response.body()!!.success) {
                    _createArticleResponse.postValue(handleCreateArticleResponse(response))
                }
            }
        } catch (t: Throwable) {
            if (t is IOException) _createArticleResponse.postValue(Resource.Error("Network Failure"))
            else _createArticleResponse.postValue(Resource.Error("Conversion Error"))
        }
    }

    private fun handleCreateArticleResponse(response: Response<ResultResponse>): Resource<ResultResponse> {
        response.body()?.let {
            createArticleResponse = it
            return Resource.Success(createArticleResponse ?: it)
        }
        return Resource.Error(response.message())
    }

    private val _tags: MutableLiveData<Resource<ArticleTags>> = MutableLiveData()
    val tags: LiveData<Resource<ArticleTags>> = _tags
    private var tagResponse: ArticleTags? = null

    private suspend fun getTags() {
        _tags.postValue(Resource.Loading())
        try {
            val response = repository.getArticleTags()
            _tags.postValue(handleTagsResponse(response))
        } catch (t: Throwable) {
            if (t is IOException)
                _tags.postValue(Resource.Error("Network Problem"))
            else
                _tags.postValue(Resource.Error("Req Conversion Error"))
        }
    }

    private fun handleTagsResponse(response: Response<ArticleTags>): Resource<ArticleTags> {
        response.body()?.let {
            tagResponse = it
            return Resource.Success(tagResponse ?: it)
        }
        return Resource.Error(response.message())
    }

    fun getArticlesByTag(tag: String) {
        viewModelScope.launch {
            getArticles(tag)
        }
    }

    private suspend fun getArticles(tag: String) {
        _articles.postValue(Resource.Loading())
        try {
            val response = repository.getArticleByTag(tag)
            _articles.postValue(handleArticles(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _articles.postValue(Resource.Error("Network Failure"))
                else -> _articles.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private val _loginUser: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginUser: LiveData<Resource<LoginResponse>> = _loginUser
    private var loginResponse: LoginResponse? = null

    fun loginUserToServer(username: String, passwd: String) {
        viewModelScope.launch {
            _loginUser.postValue(Resource.Loading())
            try {
                val response = repository.login(username, passwd)
                _loginUser.postValue(handleLogin(response))
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _loginUser.postValue(Resource.Error("Network Failure"))
                    else -> _loginUser.postValue(Resource.Error("Conversion Error"))
                }
            }
        }
    }

    private fun handleLogin(response: Response<LoginResponse>): Resource<LoginResponse> {
        response.body()?.let {
            loginResponse = it
            return Resource.Success(loginResponse ?: it)
        }
        return Resource.Error(response.message())
    }

    private val _isLoggedIn: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val isLoggedIn: LiveData<Resource<LoginResponse>> = _isLoggedIn
    private var isLoggedInResponse: LoginResponse? = null

    fun isLoggedUserLoggedIn(token: String) {
        viewModelScope.launch {
            _isLoggedIn.postValue(Resource.Loading())
            try {
                val response = repository.isLoggedIn(token)
                _isLoggedIn.postValue(handleIsLoggedIn(response))
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _isLoggedIn.postValue(Resource.Error("Network Failure"))
                    else -> _isLoggedIn.postValue(Resource.Error("Conversion Error"))
                }
            }
        }
    }

    private fun handleIsLoggedIn(response: Response<LoginResponse>): Resource<LoginResponse> {
        response.body()?.let {
            isLoggedInResponse = it
            return Resource.Success(isLoggedInResponse ?: it)
        }
        return Resource.Error(response.message())
    }

    private val _updateArticle: MutableLiveData<Resource<ResultResponse>> =
        MutableLiveData()
    val updateArticleResponse: LiveData<Resource<ResultResponse>> = _updateArticle
    private var updateArticle: ResultResponse? = null

    fun updateArticleToServer(id: String, article: SendArticle) {
        viewModelScope.launch {
            updateArticle(id, article)
        }
    }

    private suspend fun updateArticle(id: String, article: SendArticle) {
        _updateArticle.postValue(Resource.Loading())
        try {
            val response = repository.updateArticle(id, article)
            if (response.isSuccessful) {
                if (response.body()!!.success) {
                    _updateArticle.postValue(handleUpdateArticleResponse(response))
                }
            }
        } catch (t: Throwable) {
            if (t is IOException) _updateArticle.postValue(Resource.Error("Network Failure"))
            else _updateArticle.postValue(Resource.Error("Conversion Error"))
        }
    }

    private fun handleUpdateArticleResponse(response: Response<ResultResponse>): Resource<ResultResponse> {
        response.body()?.let {
            updateArticle = it
            return Resource.Success(updateArticle ?: it)
        }
        return Resource.Error(response.message())
    }
}