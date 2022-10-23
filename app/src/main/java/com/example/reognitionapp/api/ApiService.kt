package com.example.reognitionapp.api

import com.example.reognitionapp.api.dto.LoginDTO
import com.example.reognitionapp.api.responses.DogListApiResponse
import com.example.reognitionapp.api.dto.SignUpDTO
import com.example.reognitionapp.api.responses.AuthApiResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private val retrofit = Retrofit.Builder()
    .baseUrl("https://todogs.herokuapp.com/api/v1/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface ApiService {
    @GET("dogs")
    suspend fun getAllDogs(): DogListApiResponse

    @POST("sign_up")
    suspend fun signUp(@Body signUpDTO: SignUpDTO) : AuthApiResponse

    @POST("sign_in")
    suspend fun login(@Body loginDTO: LoginDTO): AuthApiResponse
}

object DogsApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}