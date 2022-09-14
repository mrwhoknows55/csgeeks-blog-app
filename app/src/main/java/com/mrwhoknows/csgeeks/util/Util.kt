package com.mrwhoknows.csgeeks.util

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun convertDateTimeToString(
        dateTimeString: String,
        inputFormatString: String,
        outputFormatString: String
    ): String {
        var date = dateTimeString
        val inputDateFormatter =
            SimpleDateFormat(inputFormatString, Locale.getDefault())
        inputDateFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val outputDateFormatter =
            SimpleDateFormat(outputFormatString, Locale.getDefault())
        outputDateFormatter.timeZone = TimeZone.getDefault()

        try {
            val dateTime = inputDateFormatter.parse(dateTimeString)
            dateTime?.let {
                date = outputDateFormatter.format(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    fun isLoading(view: View, bool: Boolean) {
        if (bool) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}