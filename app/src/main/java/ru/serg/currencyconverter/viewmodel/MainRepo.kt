package ru.serg.currencyconverter.viewmodel

import ru.serg.currencyconverter.helper.Resource
import ru.serg.currencyconverter.model.ApiResponse
import ru.serg.currencyconverter.network.ApiDataSource
import ru.serg.currencyconverter.network.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepo @Inject constructor(private val apiDataSource: ApiDataSource) : BaseDataSource() {

    suspend fun getConvertedData(
        access_key: String,
        from: String,
        to: String,
        amount: Double
    ): Flow<Resource<ApiResponse>> {
        return flow {
            emit(safeApiCall { apiDataSource.getConvertedRate(access_key, from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }

}