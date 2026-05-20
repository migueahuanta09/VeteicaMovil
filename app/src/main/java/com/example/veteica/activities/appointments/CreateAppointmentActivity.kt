package com.example.veteica.activities.appointments

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class CreateAppointmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)
        Toast.makeText(this, "Crear Cita - Próximamente", Toast.LENGTH_SHORT).show()
    }
}