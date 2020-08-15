package com.mrwhoknows.csgeeks.model


import com.google.gson.annotations.SerializedName

data class ArticleTags(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("tags")
    val tags: List<String>
)