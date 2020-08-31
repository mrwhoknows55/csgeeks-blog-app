package com.mrwhoknows.csgeeks.repository

import com.mrwhoknows.csgeeks.api.RetrofitInstance
import com.mrwhoknows.csgeeks.model.SendArticle

class BlogRepository {

    suspend fun getAllArticles() =
        RetrofitInstance.api.getAllArticles()

    suspend fun getArticleById(id: String) =
        RetrofitInstance.api.getArticleById(id)

    suspend fun searchArticles(query: String) =
        RetrofitInstance.api.searchArticles(query)

    suspend fun getArticlesByAuthor(authorName: String) =
        RetrofitInstance.api.getArticlesByAuthor(authorName)

    suspend fun orderArticlesBy(orderBy: String, order: String) =
        RetrofitInstance.api.orderArticlesBy(orderBy, order)

    suspend fun getAuthor(authorName: String) =
        RetrofitInstance.api.getAuthor(authorName)

    suspend fun createArticle(article: SendArticle, token: String) =
        RetrofitInstance.api.createArticle(article, token)

    suspend fun updateArticle(id: String, article: SendArticle, token: String) =
        RetrofitInstance.api.updateArticle(id, article, token)

    suspend fun deleteArticle(id: String, token: String) =
        RetrofitInstance.api.deleteArticle(id, token)

    suspend fun getArticleTags() =
        RetrofitInstance.api.getTags()

    suspend fun getArticleByTag(tag: String) =
        RetrofitInstance.api.getArticleByTag(tag)

    suspend fun login(username: String, passwd: String) =
        RetrofitInstance.api.login(username, passwd)

    suspend fun logoutUser(token: String) =
        RetrofitInstance.api.logoutUser(token)

    suspend fun isLoggedIn(token: String) =
        RetrofitInstance.api.isLoggedIn(token)
}