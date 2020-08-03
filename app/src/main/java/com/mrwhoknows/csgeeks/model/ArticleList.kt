package com.mrwhoknows.csgeeks.model


import com.google.gson.annotations.SerializedName

data class ArticleList(
    @SerializedName("articles")
    val articles: List<Article>,
    @SerializedName("success")
    val success: Boolean
) {
    data class Article(
        @SerializedName("author")
        val author: String,
        @SerializedName("created")
        val created: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("_id")
        val id: Int,
        @SerializedName("thumbnail")
        val thumbnail: String,
        @SerializedName("title")
        val title: String
    )
}