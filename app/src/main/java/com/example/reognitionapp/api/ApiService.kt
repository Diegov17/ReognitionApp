package com.example.reognitionapp.api

import com.example.reognitionapp.api.dto.AddDogToUserDTO
import com.example.reognitionapp.api.dto.LoginDTO
import com.example.reognitionapp.api.responses.DogListApiResponse
import com.example.reognitionapp.api.dto.SignUpDTO
import com.example.reognitionapp.api.responses.AuthApiResponse
import com.example.reognitionapp.api.responses.DefaultResponse
import com.example.reognitionapp.api.responses.DogApiResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val okHttpeClient = OkHttpClient
    .Builder()
    .addInterceptor(ApiServiceInterceptor)
    .addNetworkInterceptor(ApiServiceInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpeClient)
    .baseUrl("https://todogs.herokuapp.com/api/v1/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface ApiService {
    @GET("dogs")
    suspend fun getAllDogs(): DogListApiResponse

    @POST("sign_up")
    suspend fun signUp(@Body signUpDTO: SignUpDTO): AuthApiResponse

    @POST("sign_in")
    suspend fun login(@Body loginDTO: LoginDTO): AuthApiResponse

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}: true")
    @POST("add_dog_to_user")
    suspend fun addDogToUser(@Body addDogToUserDTO: AddDogToUserDTO): DefaultResponse

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}: true")
    @GET("get_user_dogs")
    suspend fun getUserDogs(): DogListApiResponse

    @GET("find_dog_by_ml_id")
    suspend fun getDogByMlId(@Query("ml_id") mlId: String): DogApiResponse
}

object DogsApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}