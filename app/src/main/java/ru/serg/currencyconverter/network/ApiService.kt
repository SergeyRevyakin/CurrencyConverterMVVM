package ru.serg.currencyconverter.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.serg.currencyconverter.helper.EndPoints
import ru.serg.currencyconverter.model.ApiResponse

interface ApiService {
    @GET(EndPoints.CONVERT_URL)
    suspend fun convertCurrency(
        @Query("api_key") access_key: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<ApiResponse>
}
