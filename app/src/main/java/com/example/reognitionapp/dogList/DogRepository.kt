package com.example.reognitionapp.dogList

import com.example.reognitionapp.Dog
import com.example.reognitionapp.R
import com.example.reognitionapp.api.ApiResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.reognitionapp.api.DogsApi.retrofitService
import com.example.reognitionapp.api.dto.DogDTOMapper
import com.example.reognitionapp.api.makeNetworkCall
import java.lang.Exception

class DogRepository {

    suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getAllDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }
}