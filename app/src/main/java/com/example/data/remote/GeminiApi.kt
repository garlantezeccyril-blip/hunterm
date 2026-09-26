package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class PartPayload(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentPayload(
    val role: String? = null,
    val parts: List<PartPayload>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<ContentPayload>,
    val systemInstruction: ContentPayload? = null
)

@JsonClass(generateAdapter = true)
data class CandidatePayload(
    val content: ContentPayload? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<CandidatePayload>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}
