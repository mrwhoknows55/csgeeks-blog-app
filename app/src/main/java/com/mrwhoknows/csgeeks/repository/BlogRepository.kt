package com.mrwhoknows.csgeeks.repository

import com.mrwhoknows.csgeeks.api.RetrofitInstance
import com.mrwhoknows.csgeeks.model.SendArticle

class BlogRepository {

    suspend fun getAllArticles() =
        RetrofitInstance.api.getAllArticles()

    suspend fun getArticle(id: String) =
        RetrofitInstance.api.getArticle(id)

    suspend fun getAuthor(authorName: String) =
        RetrofitInstance.api.getAuthor(authorName)

    suspend fun createArticle(article: SendArticle) =
        RetrofitInstance.api.createArticle(article)

    suspend fun updateArticle(id: String, article: SendArticle) =
        RetrofitInstance.api.updateArticle(id, article)

    suspend fun getArticleTags() =
        RetrofitInstance.api.getTags()

    suspend fun getArticleByTag(tag: String) =
        RetrofitInstance.api.getArticleByTag(tag)

    suspend fun login(username: String, passwd: String) =
        RetrofitInstance.api.login(username, passwd)

    suspend fun isLoggedIn(token: String) =
        RetrofitInstance.api.isLoggedIn(token)
}