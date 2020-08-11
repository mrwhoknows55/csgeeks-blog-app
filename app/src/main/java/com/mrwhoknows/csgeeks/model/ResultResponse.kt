package com.mrwhoknows.csgeeks.model

import com.google.gson.annotations.SerializedName

data class ResultResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("success")
    val success: Boolean
)