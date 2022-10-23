package com.example.reognitionapp.api.dto

import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

class SignUpDTO(
    val email: String,
    val password: String,
    @field:Json(name = "password_confirmation") val passwordConfirmation: String
)