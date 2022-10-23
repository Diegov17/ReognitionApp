package com.example.reognitionapp.api

import com.example.reognitionapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

private const val UNAUTHORIZED_ERROR_CODE = 401

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponseStatus<T> {
    return withContext(Dispatchers.IO) {
        try {
            ApiResponseStatus.Success(call())
        } catch (e: HttpException) {
            val errorMessage = if (e.code() == UNAUTHORIZED_ERROR_CODE) {
                R.string.error_invalid_credentials
            } else {
                R.string.error_unknown
            }
            ApiResponseStatus.Error(errorMessage)
        } catch (e: Exception) {
            val errorMessage =
                when (e.message) {
                    "sign_up_error" -> R.string.error_sign_up
                    "sign_in_error" -> R.string.error_sign_up
                    "user_already_exists" -> R.string.error_user_already_exists
                    "error_adding_dog" -> R.string.error_adding_a_dog
                    else -> R.string.error_unknown
                }
            ApiResponseStatus.Error(errorMessage)
        }
    }
}