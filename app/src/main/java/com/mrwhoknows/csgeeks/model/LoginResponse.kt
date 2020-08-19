package com.mrwhoknows.csgeeks.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("already_logged_in")
    val alreadyLoggedIn: Boolean,
    @SerializedName("author")
    val author: String,
    @SerializedName("response")
    val response: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: String
)