package com.mrwhoknows.csgeeks.util

import android.app.Activity
import android.content.SharedPreferences

class LoginInfo(activity: Activity) {

    var sharedPreferences: SharedPreferences =
        activity.getSharedPreferences(Constants.TOKEN_SHARED_PREFF, 0)

    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    val loginToken = sharedPreferences.getString(Constants.LOGIN_TOKEN, "empty")!!
    val authorName = sharedPreferences.getString(Constants.AUTHOR_NAME, "empty")!!
    val isLoggedIn = sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false)

    fun setToken(token: String) {
        editor.putString(Constants.LOGIN_TOKEN, token)
        editor.apply()
    }

    fun setAuthorName(author: String) {
        editor.putString(Constants.AUTHOR_NAME, author)
        editor.apply()
    }

    fun setIsLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(Constants.IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }
}