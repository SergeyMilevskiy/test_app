package com.android.musicmap

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://musicbrainz.org/ws/2/"

interface ApiService {

    @GET("place")
    suspend fun searchMusic(
        @Query("fmt") fmt: String = "json",
        @Query("query") query: String = ""
    ): Response<Items>
}

internal fun getApiService() = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .baseUrl(BASE_URL)
    .client(OkHttpClient
        .Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val reqBuilder = request.newBuilder()
                .header("user-agent", "android")
            chain.proceed(reqBuilder.build())
        })
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .build().create(ApiService::class.java)

data class Items(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("created")
    val created: String?,
    @SerializedName("offset")
    val offset: Int?,
    @SerializedName("places")
    val places: List<Item>?
)

data class Item(
    @SerializedName("address")
    val address: String?,
    @SerializedName("area")
    val area: Area?,
    @SerializedName("coordinates")
    val coordinates: Coordinates?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("life-span")
    val lifeSpan: LifeSpanX?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("score")
    val score: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("type-id")
    val typeId: String?
)

data class Area(
    @SerializedName("id")
    val id: String?,
    @SerializedName("life-span")
    val lifeSpan: LifeSpan?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("sort-name")
    val sortName: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("type-id")
    val typeId: String?
)

data class Coordinates(
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?
)

data class LifeSpanX(
    @SerializedName("begin")
    val begin: String?,
    @SerializedName("ended")
    val ended: String?
)

data class LifeSpan(
    @SerializedName("begin")
    val begin: String?,
    @SerializedName("ended")
    val ended: String?
)