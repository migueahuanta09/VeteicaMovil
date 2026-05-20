package com.example.veteica.activities.owners

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class EditOwnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_owner)
        Toast.makeText(this, "Editar Dueño - Próximamente", Toast.LENGTH_SHORT).show()
    }
}