package com.example.reognitionapp.auth

import com.example.reognitionapp.domain.User
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.api.DogsApi
import com.example.reognitionapp.api.dto.LoginDTO
import com.example.reognitionapp.api.dto.SignUpDTO
import com.example.reognitionapp.api.dto.UserDTOMapper
import com.example.reognitionapp.api.makeNetworkCall
import java.lang.Exception

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): ApiResponseStatus<User> = makeNetworkCall {
        val loginDTO = LoginDTO(email, password)
        val loginResponse = DogsApi.retrofitService.login(loginDTO)

        if (!loginResponse.isSuccess)
            throw Exception(loginResponse.message)

        val userDTO = loginResponse.data.user
        val userDTOMapper = UserDTOMapper()
        userDTOMapper.fromUserDTOToUserDomain(userDTO)
    }

    suspend fun signUp(
        email: String,
        password: String,
        passwordConfirmation: String
    ): ApiResponseStatus<User> = makeNetworkCall {
        val signUpDTO = SignUpDTO(email, password, passwordConfirmation)
        val signUpResponse = DogsApi.retrofitService.signUp(signUpDTO)

        if (!signUpResponse.isSuccess)
            throw Exception(signUpResponse.message)

        val userDTO = signUpResponse.data.user
        val userDTOMapper = UserDTOMapper()
        userDTOMapper.fromUserDTOToUserDomain(userDTO)
    }
}