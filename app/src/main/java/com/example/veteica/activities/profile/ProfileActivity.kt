package com.example.veteica.activities.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Toast.makeText(this, "Perfil de Usuario - Próximamente", Toast.LENGTH_SHORT).show()
    }
}