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

}