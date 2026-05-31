package com.example.veteica.models

data class Pet(
    val id: Int = 0,
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val gender: String = "",
    val color: String = "",
    val ownerName: String = "",
    val notes: String = "",
    val photoUri: String? = null
)