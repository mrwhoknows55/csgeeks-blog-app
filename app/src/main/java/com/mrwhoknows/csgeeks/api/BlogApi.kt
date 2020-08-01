package com.mrwhoknows.csgeeks.api

import com.mrwhoknows.csgeeks.model.PostMeta
import retrofit2.Response
import retrofit2.http.GET

interface BlogApi {

    @GET("blog/posts")
    suspend fun getAllPosts(): Response<PostMeta>
}