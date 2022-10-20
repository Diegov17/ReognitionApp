package com.example.reognitionapp

data class Dog(
    val id: Long, val index: Int, val name: String, val type: String, val heightFemale: Double,
    val heightMale: Double, val imgUrl: String, val lifeExpectancy: String, val temperament: String,
    val weightFemale: Double, val weightMale: Double) {
}