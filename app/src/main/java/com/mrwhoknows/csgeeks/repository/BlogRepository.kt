package com.mrwhoknows.csgeeks.repository

import com.mrwhoknows.csgeeks.api.RetrofitInstance
import com.mrwhoknows.csgeeks.model.Article

class BlogRepository {

    suspend fun getAllArticles() =
        RetrofitInstance.api.getAllArticles()

    suspend fun getArticle(id: String) =
        RetrofitInstance.api.getArticle(id)

    suspend fun getAuthor(authorName: String) =
        RetrofitInstance.api.getAuthor(authorName)

    suspend fun createArticle(article: Article.Article) =
        RetrofitInstance.api.createArticle(
            article.title,
            article.content,
            article.author,
            article.description,
            article.thumbnail,
            //TODO fix this tags issue
            article.tags[0]
        )
}