package ru.serg.currencyconverter.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.serg.currencyconverter.helper.Resource
import ru.serg.currencyconverter.helper.SingleLiveEvent
import ru.serg.currencyconverter.model.ApiResponse
import ru.serg.currencyconverter.model.Rates
import ru.serg.currencyconverter.room.AppDatabase
import ru.serg.currencyconverter.room.HistoryRepository
import ru.serg.currencyconverter.room.Operation

class MainViewModel @ViewModelInject constructor(
    private val mainRepo: MainRepo,
    application: Application
) : ViewModel() {

    private val _data = SingleLiveEvent<Resource<ApiResponse>>()
    private val rates = MutableLiveData<HashMap<String, Rates>>()

    private val historyRepository: HistoryRepository
    val history: LiveData<List<Operation>>

    init {
        val historyDao = AppDatabase.getAppDatabase(application)!!.historyDao()
        historyRepository = HistoryRepository(historyDao)
        history = historyRepository.getHistory
    }

    val data = _data
    val convertedRate = MutableLiveData<Double>()

    fun getConvertedData(access_key: String, from: String, to: String, amount: Double) {
        viewModelScope.launch {
            mainRepo.getConvertedData(access_key, from, to, amount).collect {
                data.value = it
                historyRepository.insert(
                    Operation(
                        fromName = from,
                        fromAmount = amount,
                        toName = to,
                        toAmount = convertedRate.value!!
                    )
                )
            }
        }
    }

    fun dropDatabase() {
        viewModelScope.launch {
            historyRepository.dropTable()
        }
    }
}