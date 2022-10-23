package com.example.reognitionapp.dogList

import com.example.reognitionapp.R
import com.example.reognitionapp.domain.Dog
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.api.DogsApi.retrofitService
import com.example.reognitionapp.api.dto.AddDogToUserDTO
import com.example.reognitionapp.api.dto.DogDTOMapper
import com.example.reognitionapp.api.makeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.lang.Exception

class DogRepository {

    suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
        return withContext(Dispatchers.IO) {
            val allDogsListDeferred = async { downloadDogs() }
            //val userDogsListDeferred = async { getUserDogs() }
            val userDogsListResponse = getMockedUserDogs()

            val allDogsListResponse = allDogsListDeferred.await()
            //val userDogsListResponse = userDogsListDeferred.await()

            when {
                allDogsListResponse is ApiResponseStatus.Error -> allDogsListResponse
                userDogsListResponse is ApiResponseStatus.Error -> userDogsListResponse
                allDogsListResponse is ApiResponseStatus.Success && userDogsListResponse is ApiResponseStatus.Success -> {
                    ApiResponseStatus.Success(
                        getCollectionList(
                            allDogsListResponse.data,
                            userDogsListResponse.data
                        )
                    )
                }
                else -> {
                    ApiResponseStatus.Error(R.string.error_unknown)
                }
            }
        }
    }

    private fun getCollectionList(allDogList: List<Dog>, userDogList: List<Dog>): List<Dog> {
        return allDogList.map {
            if (userDogList.contains(it)) {
                it
            } else {
                Dog(
                    it.id, it.index, "", "", "", "", "",
                    "", "", "", "", inCollection = false
                )
            }
        }.sorted()
    }

    private suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getAllDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }

    suspend fun getUserDogs(): ApiResponseStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponse = retrofitService.getUserDogs()
        val dogDTOList = dogListApiResponse.data.dogs
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
    }

    private fun getMockedUserDogs(): ApiResponseStatus<List<Dog>> {
        return ApiResponseStatus.Success(getFakeUserDogs())
    }

    suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> = makeNetworkCall {
        val addDogToUserDTO = AddDogToUserDTO(dogId)
        val addDogToUserResponse = retrofitService.addDogToUser(addDogToUserDTO)

        if (!addDogToUserResponse.isSuccess) {
            throw Exception(addDogToUserResponse.message)
        }
    }

    private fun getFakeUserDogs(): List<Dog> {
        val dogList = mutableListOf<Dog>()
        dogList.add(
            Dog(
                8L,
                4,
                "Pekinese",
                "Toy",
                "25.3",
                "28.0",
                "https://firebasestorage.googleapis.com/v0/b/perrodex-app.appspot.com/o/dog_details_images%2Fn02086079-pekinese.png?alt=media&token=f3cb4225-6690-42f2-a492-b77fcdeb5ee3",
                "10 - 12",
                "Juguetón, feliz, amistoso",
                "4 kg",
                "5kg "
            )
        )
        dogList.add(
            Dog(
                9L,
                5,
                "Shih Tzu",
                "Toy",
                "25.3",
                "28.0",
                "https://firebasestorage.googleapis.com/v0/b/perrodex-app.appspot.com/o/dog_details_images%2Fn02086240-shih-tzu.png?alt=media&token=dc1c9902-1d65-4772-9ed4-c4ff5cda74f9",
                "10 - 12",
                "Juguetón, feliz, amistoso",
                "4 kg",
                "5 kg"
            )
        )
        return dogList
    }
}