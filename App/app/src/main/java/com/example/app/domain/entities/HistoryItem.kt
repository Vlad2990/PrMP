package com.example.app.domain.entities

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Serializable
data class HistoryItem (
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
    ) {
    fun getFormattedDate(): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
}