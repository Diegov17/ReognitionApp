package com.example.reognitionapp.dogList

import com.example.reognitionapp.Dog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.reognitionapp.api.DogsApi.retrofitService
import com.example.reognitionapp.api.dto.DogDTOMapper

class DogRepository {

    suspend fun downloadDogs(): List<Dog> {
        return withContext(Dispatchers.IO) {
            val dogListApiResponse = retrofitService.getAllDogs()
            val dogDTOList = dogListApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }
    }
}