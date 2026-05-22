package com.example.veteica.activities.payments

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class CreatePaymentActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etProductName: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etDosage: EditText
    private lateinit var etPrice: EditText
    private lateinit var etIndications: EditText
    private lateinit var etFormula: EditText
    private lateinit var etAdministration: EditText
    private lateinit var etStock: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_payment)

        initViews()
        setupToolbar()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        etProductName = findViewById(R.id.etProductName)
        etExpiryDate = findViewById(R.id.etExpiryDate)
        etDosage = findViewById(R.id.etDosage)
        etPrice = findViewById(R.id.etPrice)
        etIndications = findViewById(R.id.etIndications)
        etFormula = findViewById(R.id.etFormula)
        etAdministration = findViewById(R.id.etAdministration)
        etStock = findViewById(R.id.etStock)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSaveToolbar.setOnClickListener {
            saveProduct()
        }

        btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        val name = etProductName.text.toString().trim()
        val expiryDate = etExpiryDate.text.toString().trim()
        val dosage = etDosage.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val indications = etIndications.text.toString().trim()
        val formula = etFormula.text.toString().trim()
        val administration = etAdministration.text.toString().trim()
        val stock = etStock.text.toString().trim()

        when {
            name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del producto", Toast.LENGTH_SHORT).show()
            price.isEmpty() -> Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Producto $name creado correctamente", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}