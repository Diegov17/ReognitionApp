package com.example.reognitionapp.dogList

import com.example.reognitionapp.domain.Dog
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.api.DogsApi.retrofitService
import com.example.reognitionapp.api.dto.DogDTOMapper
import com.example.reognitionapp.api.makeNetworkCall

class DogRepository {

    suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getAllDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }
}