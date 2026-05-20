package com.example.veteica.activities.payments

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class EditPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_payment)
        Toast.makeText(this, "Editar Cobro - Próximamente", Toast.LENGTH_SHORT).show()
    }
}