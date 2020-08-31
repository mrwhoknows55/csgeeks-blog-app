package com.mrwhoknows.csgeeks.api

import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.model.ArticleTags
import com.mrwhoknows.csgeeks.model.Author
import com.mrwhoknows.csgeeks.model.LoginResponse
import com.mrwhoknows.csgeeks.model.ResultResponse
import com.mrwhoknows.csgeeks.model.SendArticle
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface BlogApi {

    @GET("blog/posts")
    suspend fun getAllArticles(): Response<ArticleList>

    @GET("blog/post")
    suspend fun getArticleById(
        @Query("id")
        articleID: String
    ): Response<Article>

    @GET("blog/posts")
    suspend fun getArticleByTag(
        @Query("tag")
        tag: String
    ): Response<ArticleList>

    @GET("blog/posts")
    suspend fun getArticlesByAuthor(
        @Query("author")
        author: String
    ): Response<ArticleList>

    @GET("blog/posts")
    suspend fun orderArticlesBy(
        @Query("orderby")
        orderBy: String,
        @Query("order")
        order: String
    ): Response<ArticleList>

    @GET("blog/posts")
    suspend fun searchArticles(
        @Query("search")
        query: String
    ): Response<ArticleList>

    @GET("blog/author")
    suspend fun getAuthor(
        @Query("name")
        authorName: String
    ): Response<Author>

    @POST("blog/create")
    suspend fun createArticle(
        @Body
        article: SendArticle,
        @Query("token")
        token: String
    ): Response<ResultResponse>

    @POST("blog/post/delete")
    suspend fun deleteArticle(
        @Query("id")
        id: String,
        @Query("token")
        token: String
    ): Response<ResultResponse>

    @PUT("blog/update")
    suspend fun updateArticle(
        @Query("id")
        id: String,
        @Body
        article: SendArticle,
        @Query("token")
        token: String
    ): Response<ResultResponse>

    @GET("blog")
    suspend fun getTags(
        @Query("get")
        tags: String = "tags"
    ): Response<ArticleTags>

    @Multipart
    @POST("blog/login")
    suspend fun login(
        @Part("username")
        username: String,
        @Part("password")
        passwd: String
    ): Response<LoginResponse>

    @POST("/blog/logout")
    suspend fun logoutUser(
        @Query("token")
        token: String
    ): Response<LoginResponse>

    @GET("/blog/login/check")
    suspend fun isLoggedIn(
        @Query("token")
        token: String
    ): Response<LoginResponse>
}