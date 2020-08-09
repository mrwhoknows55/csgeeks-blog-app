package com.mrwhoknows.csgeeks.model


import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("author")
    val author: List<Author>,
    @SerializedName("success")
    val success: Boolean
) {
    data class Author(
        @SerializedName("auth_id")
        val authId: Int,
        @SerializedName("bio")
        val bio: String,
        @SerializedName("mail")
        val mail: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("social")
        val social: List<List<String>>
    )
}