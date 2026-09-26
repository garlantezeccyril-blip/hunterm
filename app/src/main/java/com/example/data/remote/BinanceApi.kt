package com.example.data.remote

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Binance24hrTicker(
    val symbol: String,
    val lastPrice: String,
    val priceChangePercent: String,
    val volume: String,
    val quoteVolume: String,
    val highPrice: String,
    val lowPrice: String,
    val count: Long? = null
)

interface BinanceApiService {
    @GET("api/v3/ticker/24hr")
    suspend fun get24hrTickers(): List<Binance24hrTicker>

    // Klines format: [ [OpenTime, Open, High, Low, Close, Volume, CloseTime, ...], ... ]
    @GET("api/v3/klines")
    suspend fun getKlines(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "15m",
        @Query("limit") limit: Int = 50
    ): List<List<Any>>
}

object BinanceClient {
    private const val BASE_URL = "https://api.binance.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val service: BinanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BinanceApiService::class.java)
    }
}
