package com.mrwhoknows.csgeeks.api

import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.model.ArticleTags
import com.mrwhoknows.csgeeks.model.Author
import com.mrwhoknows.csgeeks.model.LoginResponse
import com.mrwhoknows.csgeeks.model.ResultResponse
import com.mrwhoknows.csgeeks.model.SendArticle
import com.mrwhoknows.csgeeks.util.Keys.C_AUTH
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface BlogApi {

    @GET("blog/posts")
    suspend fun getAllArticles(): Response<ArticleList>

    @GET("blog/post")
    suspend fun getArticle(
        @Query("id")
        articleID: String
    ): Response<Article>

    @GET("blog/author")
    suspend fun getAuthor(
        @Query("name")
        authorName: String
    ): Response<Author>

    @POST("blog/create")
    @Headers("C_AUTH: $C_AUTH")
    suspend fun createArticle(
        @Body
        article: SendArticle
    ): Response<ResultResponse>

    @GET("blog")
    suspend fun getTags(
        @Query("get")
        tags: String = "tags"
    ): Response<ArticleTags>

    @GET("blog/posts")
    suspend fun getArticleByTag(
        @Query("tag")
        tag: String
    ): Response<ArticleList>

    @Multipart
    @POST("blog/login")
    suspend fun login(
        @Part("username")
        username: String,
        @Part("password")
        passwd: String
    ): Response<LoginResponse>
}