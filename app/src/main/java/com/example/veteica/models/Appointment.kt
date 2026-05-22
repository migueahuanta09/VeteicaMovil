package com.example.veteica.models

data class Appointment(
    val id: Int = 0,
    val date: String = "",
    val time: String = "",
    val petName: String = "",
    val ownerName: String = "",
    val veterinarian: String = "",
    val reason: String = "",
    val status: String = ""
)