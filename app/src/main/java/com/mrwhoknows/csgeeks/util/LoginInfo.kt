package com.mrwhoknows.csgeeks.util

import android.app.Activity
import android.content.SharedPreferences

class LoginInfo(activity: Activity) {

    var sharedPreferences: SharedPreferences =
        activity.getSharedPreferences(Constants.TOKEN_SHARED_PREFF, 0)

    val loginToken = sharedPreferences.getString(Constants.LOGIN_TOKEN, "empty")!!
    val authorName = sharedPreferences.getString(Constants.AUTHOR_NAME, "empty")!!
    val isLoggedIn = sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false)
}