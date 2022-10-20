package com.example.reognitionapp.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .baseUrl("https://todogs.herokuapp.com/api/v1/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface ApiService {
    @GET("dogs")
    suspend fun getAllDogs()
}

object DogsApi {

    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}