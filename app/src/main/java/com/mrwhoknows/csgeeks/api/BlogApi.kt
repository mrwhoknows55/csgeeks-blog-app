package com.mrwhoknows.csgeeks.api

import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.model.Author
import com.mrwhoknows.csgeeks.model.ResultResponse
import com.mrwhoknows.csgeeks.util.Keys.C_AUTH
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
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

    @Multipart
    @POST("blog/create")
    @Headers("C_AUTH: $C_AUTH")
    suspend fun createArticle(
        //TODO add these in constants
        @Part("title") title: String,
        @Part("content") content: String,
        @Part("author") author: String,
        @Part("description") desc: String,
        @Part("thumbnail") thumbnail: String,
        @Part("tags") tags: String
    ): Response<ResultResponse>
}