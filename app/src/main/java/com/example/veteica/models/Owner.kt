package com.example.veteica.models

data class Owner(
    val id: Int = 0,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val petsCount: Int = 0,
    val photoUri: String? = null
)