package com.mrwhoknows.csgeeks.repository

import com.mrwhoknows.csgeeks.api.RetrofitInstance
import com.mrwhoknows.csgeeks.model.SendArticle

class BlogRepositoryImpl : BlogRepository {

    override suspend fun getAllArticles() =
        RetrofitInstance.api.getAllArticles()

    override suspend fun getArticleById(id: String) =
        RetrofitInstance.api.getArticleById(id)

    override suspend fun searchArticles(query: String) =
        RetrofitInstance.api.searchArticles(query)

    override suspend fun getArticlesByAuthor(authorName: String) =
        RetrofitInstance.api.getArticlesByAuthor(authorName)

    override suspend fun orderArticlesBy(orderBy: String, order: String) =
        RetrofitInstance.api.orderArticlesBy(orderBy, order)

    override suspend fun orderArticlesBy(tag:String, orderBy: String, order: String) =
        RetrofitInstance.api.orderArticlesBy(tag, orderBy, order)

    override suspend fun getAuthor(authorName: String) =
        RetrofitInstance.api.getAuthor(authorName)

    override suspend fun createArticle(article: SendArticle, token: String) =
        RetrofitInstance.api.createArticle(article, token)

    override suspend fun updateArticle(id: String, article: SendArticle, token: String) =
        RetrofitInstance.api.updateArticle(id, article, token)

    override suspend fun deleteArticle(id: String, token: String) =
        RetrofitInstance.api.deleteArticle(id, token)

    override suspend fun getArticleTags() =
        RetrofitInstance.api.getTags()

    override suspend fun getArticleByTag(tag: String) =
        RetrofitInstance.api.getArticleByTag(tag)

    override suspend fun login(username: String, passwd: String) =
        RetrofitInstance.api.login(username, passwd)

    override suspend fun logoutUser(token: String) =
        RetrofitInstance.api.logoutUser(token)

    override suspend fun isLoggedIn(token: String) =
        RetrofitInstance.api.isLoggedIn(token)
}