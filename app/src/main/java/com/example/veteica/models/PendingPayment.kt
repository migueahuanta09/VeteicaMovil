package com.example.veteica.models

data class PendingPayment(
    val id: Int,
    val petName: String,
    val serviceName: String,
    val date: String,
    val total: Double,
    var status: String  // Cambiado de val a var para poder modificarlo
)