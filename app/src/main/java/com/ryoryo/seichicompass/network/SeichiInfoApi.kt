package com.ryoryo.seichicompass.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object SeichiInfoApi {
    private const val API_BASE_URL = "http://www.ry-tom.mydns.jp:3333"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(API_BASE_URL)
        .build()

    val retrofitService: SeichiInfoApiService by lazy {
        retrofit.create(SeichiInfoApiService::class.java)
    }
}