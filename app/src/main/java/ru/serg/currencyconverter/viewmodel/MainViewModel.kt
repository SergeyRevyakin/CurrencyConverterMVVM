package ru.serg.currencyconverter.viewmodel

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.serg.currencyconverter.helper.Resource
import ru.serg.currencyconverter.helper.SingleLiveEvent
import ru.serg.currencyconverter.model.ApiResponse
import ru.serg.currencyconverter.model.Rates

class MainViewModel @ViewModelInject constructor(private val mainRepo: MainRepo) : ViewModel() {

    private val _data = SingleLiveEvent<Resource<ApiResponse>>()
    private val rates = MutableLiveData<HashMap<String, Rates>>()

    val data = _data
    val convertedRate = MutableLiveData<Double>()

    fun getConvertedData(access_key: String, from: String, to: String, amount: Double) {
        viewModelScope.launch {
            mainRepo.getConvertedData(access_key, from, to, amount).collect {
                data.value = it
            }
        }
    }
}