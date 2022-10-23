package com.example.reognitionapp.di

import com.example.reognitionapp.api.ApiService
import com.example.reognitionapp.api.ApiServiceInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://todogs.herokuapp.com/api/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    fun provideOkHttpClient() = OkHttpClient
        .Builder()
        .addInterceptor(ApiServiceInterceptor)
        .addNetworkInterceptor(ApiServiceInterceptor)
        .build()
}