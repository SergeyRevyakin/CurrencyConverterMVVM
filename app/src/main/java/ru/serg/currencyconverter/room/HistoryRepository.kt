package ru.serg.currencyconverter.room

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository(private val historyDao: HistoryDao) {
    val getHistory = historyDao.getAllOperations()

    suspend fun insert(operation: Operation) {
        historyDao.insert(operation)
    }

    suspend fun dropTable() = withContext(Dispatchers.Default) {
        historyDao.dropTable()
    }
}