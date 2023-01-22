package com.mrwhoknows.csgeeks.repository

import com.mrwhoknows.csgeeks.model.*
import retrofit2.Response

interface BlogRepository {

    suspend fun getAllArticles(): Response<ArticleList>

    suspend fun getArticleById(id: String): Response<Article>

    suspend fun searchArticles(query: String): Response<ArticleList>

    suspend fun getArticlesByAuthor(authorName: String): Response<ArticleList>

    suspend fun orderArticlesBy(orderBy: String, order: String): Response<ArticleList>

    suspend fun orderArticlesBy(tag: String, orderBy: String, order: String): Response<ArticleList>

    suspend fun getAuthor(authorName: String): Response<Author>

    suspend fun createArticle(article: SendArticle, token: String): Response<ResultResponse>

    suspend fun updateArticle(
        id: String, article: SendArticle, token: String
    ): Response<ResultResponse>

    suspend fun deleteArticle(id: String, token: String): Response<ResultResponse>

    suspend fun getArticleTags(): Response<ArticleTags>

    suspend fun getArticleByTag(tag: String): Response<ArticleList>

    suspend fun login(username: String, passwd: String): Response<LoginResponse>

    suspend fun logoutUser(token: String): Response<LoginResponse>

    suspend fun isLoggedIn(token: String): Response<LoginResponse>
}