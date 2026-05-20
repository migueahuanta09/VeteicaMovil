package com.example.veteica.activities.owners

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class CreateOwnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_owner)
        Toast.makeText(this, "Crear Dueño - Próximamente", Toast.LENGTH_SHORT).show()
    }
}