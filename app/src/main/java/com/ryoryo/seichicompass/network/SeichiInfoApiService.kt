package com.ryoryo.seichicompass.network

import com.ryoryo.seichicompass.model.NewSeichiInfo
import com.ryoryo.seichicompass.model.SeichiInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Reference for Retrofit
 * - https://square.github.io/retrofit/
 * - https://developer.android.com/codelabs/basic-android-kotlin-compose-getting-data-internet?hl=ja#7
 * - https://qiita.com/hiesiea/items/d1d8fff1d4a4ed12fbfb
 */
interface SeichiInfoApiService {
    @GET("api/seichis")
    suspend fun getSeichis(): List<SeichiInfo>

    @POST("api/seichis")
    suspend fun postSeichi(@Body newInfo: NewSeichiInfo)
}