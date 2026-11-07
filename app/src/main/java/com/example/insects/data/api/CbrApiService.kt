package com.example.insects.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface CbrApiService {
    @GET("scripts/xml_metall.asp")
    suspend fun getMetallRatesRaw(
        @Query("date_req1") dateReq1: String,
        @Query("date_req2") dateReq2: String
    ): String
}

object CbrRetrofitClient {
    private const val BASE_URL = "https://www.cbr.ru/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: CbrApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(CbrApiService::class.java)
    }
}