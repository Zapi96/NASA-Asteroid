package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = Constants.BASE_URL


// Use Moshi Builder to create a Moshi object with the KotlinAdapterFactory
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Use Retrofit Builder with ScalarsConverterFactory and BASE_URL
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi ))
    .baseUrl(BASE_URL)
    .build()

interface PictureApiService {
    @GET("planetary/apod")
    suspend fun getPicture(@Query("api_key") apiKey: String): PictureOfDay
}

// Create the MarsApi object using Retrofit ro implement the MarsApiService
object PictureApi {
    val retrofitService: PictureApiService by lazy {
        retrofit.create(PictureApiService::class.java)
    }
}