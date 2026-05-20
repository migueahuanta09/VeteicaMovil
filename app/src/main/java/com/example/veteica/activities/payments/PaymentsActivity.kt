package com.example.veteica.activities.payments

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class PaymentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)
        Toast.makeText(this, "Cobros - Próximamente", Toast.LENGTH_SHORT).show()
    }
}