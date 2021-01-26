package ru.serg.currencyconverter.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: Operation)

    @Query("SELECT * FROM Operation")
    fun getAllOperations(): LiveData<List<Operation>>

    @Query("DELETE FROM Operation")
    fun dropTable()
}