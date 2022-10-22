package com.example.reognitionapp.api

import com.example.reognitionapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponseStatus<T> {
    return withContext(Dispatchers.IO) {
        try {
            ApiResponseStatus.Success(call())
        } catch (e: Exception) {
            ApiResponseStatus.Error(R.string.exception_error_message)
        }
    }
}