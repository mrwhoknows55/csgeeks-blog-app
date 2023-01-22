package com.mrwhoknows.csgeeks.util

import com.mrwhoknows.csgeeks.BuildConfig

object Constants {
    const val APP_THEME_SHARED_PREFS = "AppTheme"
    const val NIGHT_MODE = "NightMode"
    const val BASE_API_URL = BuildConfig.BASE_API_URL
    const val BLOG_SITE_URL_WITHOUT_HTTPS: String = BuildConfig.BLOG_LINK_WITHOUT_HTTP
    const val BLOG_SITE_URL_WITH_HTTPS = "https://$BLOG_SITE_URL_WITHOUT_HTTPS"

    const val TOKEN_SHARED_PREFF = "TOKEN"
    const val IS_LOGGED_IN = "IS_LOGGED_IN"
    const val LOGIN_TOKEN = "LOGIN_TOKEN"
    const val AUTHOR_NAME = "AUTHOR_NAME"
}