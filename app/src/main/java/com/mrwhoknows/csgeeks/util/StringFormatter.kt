package com.mrwhoknows.csgeeks.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object StringFormatter {

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
}