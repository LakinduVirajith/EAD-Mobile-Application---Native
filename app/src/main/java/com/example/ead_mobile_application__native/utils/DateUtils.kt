package com.example.ead_mobile_application__native.utils

import java.text.SimpleDateFormat
import java.util.Locale

class DateUtils {
    // FORMATS THE INPUT DATE STRING FROM THE SPECIFIED INPUT FORMAT
    companion object {
        fun formatDate(inputDate: String, inputFormat: String, outputFormat: String): String? {
            return try {
                val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
                val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
                val date = inputDateFormat.parse(inputDate)
                date?.let { outputDateFormat.format(it) }
            } catch (e: Exception) {
                null
            }
        }
    }
}