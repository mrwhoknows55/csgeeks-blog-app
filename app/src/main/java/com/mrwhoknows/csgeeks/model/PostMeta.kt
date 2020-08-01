package com.mrwhoknows.csgeeks.model


import com.google.gson.annotations.SerializedName

data class PostMeta(
    @SerializedName("posts")
    val posts: List<Post>
) {
    data class Post(
        @SerializedName("author")
        val author: String,
        @SerializedName("_id")
        val id: Int,
        @SerializedName("title")
        val title: String
    )
}