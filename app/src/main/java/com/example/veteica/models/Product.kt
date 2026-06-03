package com.example.veteica.models

data class Product(
    val id: Int = 0,
    val mongoId: String = "",
    val name: String = "",
    val stock: Int = 0,
    val price: Double = 0.0,
    val expiryDate: String = "",
    val dose: String = "",
    val indications: String = "",
    val formula: String = "",
    val administration: String = ""
)