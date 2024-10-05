package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // Adjust format as needed

    @FromJson
    fun fromJson(dateString: String): Date? {
        return dateFormat.parse(dateString)
    }

    @ToJson
    fun toJson(date: Date): String {
        return dateFormat.format(date)
    }
}