package com.example.veteica.models

data class Payment(
    val id: Int = 0,
    val concept: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val petName: String = "",
    val method: String = ""
)