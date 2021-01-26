package ru.serg.currencyconverter.room

class HistoryRepository(private val historyDao: HistoryDao) {
    val getHistory = historyDao.getAllOperations()

    suspend fun insert(operation: Operation) {
        historyDao.insert(operation)
    }

    fun dropTable() {
        historyDao.dropTable()
    }
}