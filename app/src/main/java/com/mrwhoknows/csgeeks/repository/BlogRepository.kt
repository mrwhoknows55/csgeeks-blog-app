package com.mrwhoknows.csgeeks.repository

import com.mrwhoknows.csgeeks.api.RetrofitInstance

class BlogRepository {

    suspend fun getAllArticles() =
        RetrofitInstance.api.getAllPosts()

    suspend fun getArticle(id: String) =
        RetrofitInstance.api.getPostById(id)
}