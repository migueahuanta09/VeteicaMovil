package com.example.veteica.models

data class OwnerAppointment(
    val id: Int,
    val consulta: String,
    val fecha: String,
    val diagnostico: String,
    val veterinario: String
)