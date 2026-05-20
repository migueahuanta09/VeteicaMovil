package com.example.veteica.models

data class Appointment(
    val id: Int = 0,
    val petName: String = "",
    val date: String = "",
    val time: String = "",
    val reason: String = "",
    val status: String = ""
)