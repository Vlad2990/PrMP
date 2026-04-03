package com.example.app.domain.interfaces

import com.example.app.domain.entities.HistoryItem

interface HistoryRepositoryInterface {
    suspend fun GetHistory(): List<HistoryItem>
    suspend fun SaveToHistory(item: HistoryItem)
}