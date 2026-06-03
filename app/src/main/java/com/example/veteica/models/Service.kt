package com.example.veteica.models

data class Service(
    val id: Int = 0,
    val mongoId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val vets: Int = 0,
    val price: Double = 0.0
)