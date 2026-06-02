package com.example.veteica.activities.payments

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class CreateServiceActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etServiceName: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etVeterinarians: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerServiceType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_service)

        initViews()
        setupToolbar()
        setupSpinner()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        etServiceName = findViewById(R.id.etServiceName)
        etQuantity = findViewById(R.id.etQuantity)
        etVeterinarians = findViewById(R.id.etVeterinarians)
        etPrice = findViewById(R.id.etPrice)
        etDescription = findViewById(R.id.etDescription)
        spinnerServiceType = findViewById(R.id.spinnerServiceType)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinner() {
        val serviceTypes = arrayOf("Consulta", "Cirugía", "Observación", "Vacunación", "Terapia", "Otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServiceType.adapter = adapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }



        btnSave.setOnClickListener {
            saveService()
        }
    }

    private fun saveService() {
        val name = etServiceName.text.toString().trim()
        val quantity = etQuantity.text.toString().trim()
        val veterinarians = etVeterinarians.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val serviceType = spinnerServiceType.selectedItem.toString()

        when {
            name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del servicio", Toast.LENGTH_SHORT).show()
            quantity.isEmpty() -> Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show()
            veterinarians.isEmpty() -> Toast.makeText(this, "Ingresa el número de veterinarios", Toast.LENGTH_SHORT).show()
            price.isEmpty() -> Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Servicio $name creado correctamente\nTipo: $serviceType", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}