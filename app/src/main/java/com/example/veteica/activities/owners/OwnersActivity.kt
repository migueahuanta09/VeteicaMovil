package com.example.veteica.activities.owners

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class OwnersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owners)
        Toast.makeText(this, "Dueños - Próximamente", Toast.LENGTH_SHORT).show()
    }
}