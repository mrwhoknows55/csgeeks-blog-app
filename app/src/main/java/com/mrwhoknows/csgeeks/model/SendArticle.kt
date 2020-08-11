package com.mrwhoknows.csgeeks.model

import com.google.gson.annotations.SerializedName

data class SendArticle(
    @SerializedName("author")
    val author: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("title")
    val title: String
)