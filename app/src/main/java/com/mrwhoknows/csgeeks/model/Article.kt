package com.mrwhoknows.csgeeks.model


import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("article")
    val article: Article,
    @SerializedName("success")
    val success: Boolean
) {
    data class Article(
        @SerializedName("author")
        val author: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("created")
        val created: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("_id")
        val id: Int,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("thumbnail")
        val thumbnail: String,
        @SerializedName("title")
        val title: String
    )
}