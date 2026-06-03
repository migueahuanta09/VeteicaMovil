package com.example.veteica.models

data class PendingPayment(
    val id: Int = 0,
    val mongoId: String = "",
    val petName: String = "",
    val serviceName: String = "",
    val date: String = "",
    val total: Double = 0.0,
    var status: String = "Pendiente"
)