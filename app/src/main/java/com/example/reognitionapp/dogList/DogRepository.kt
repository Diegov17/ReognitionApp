package com.example.reognitionapp.dogList

import com.example.reognitionapp.Dog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.reognitionapp.api.DogsApi.retrofitService

class DogRepository {

    suspend fun downloadDogs(): List<Dog> {
        return withContext(Dispatchers.IO) {
            val dogListApiResponse = retrofitService.getAllDogs()
            dogListApiResponse.data.dogs
        }
    }
}