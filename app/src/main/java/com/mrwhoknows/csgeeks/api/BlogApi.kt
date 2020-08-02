package com.mrwhoknows.csgeeks.api

import com.mrwhoknows.csgeeks.model.Article
import com.mrwhoknows.csgeeks.model.ArticleList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApi {

    @GET("blog/posts")
    suspend fun getAllPosts(): Response<ArticleList>

    @GET("blog/post")
    suspend fun getPostById(
        @Query("id")
        articleID: String
    ): Response<Article>
}