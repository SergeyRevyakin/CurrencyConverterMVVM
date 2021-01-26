package ru.serg.currencyconverter.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Operation(
    val fromName: String,
    val fromAmount: Double,
    val toName: String,
    val toAmount: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)