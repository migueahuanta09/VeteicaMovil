package com.example.veteica.activities.payments

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class EditServiceActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etServiceName: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etVeterinarians: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText

    private var serviceId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)

        initViews()
        setupToolbar()
        loadServiceData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        etServiceName = findViewById(R.id.etServiceName)
        etQuantity = findViewById(R.id.etQuantity)
        etVeterinarians = findViewById(R.id.etVeterinarians)
        etPrice = findViewById(R.id.etPrice)
        etDescription = findViewById(R.id.etDescription)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val serviceName = intent.getStringExtra("service_name") ?: "Servicio"
        val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        toolbarTitle.text = "Editar $serviceName"
    }

    private fun loadServiceData() {
        serviceId = intent.getIntExtra("service_id", 1)
        val serviceName = intent.getStringExtra("service_name") ?: "Citas"

        etServiceName.setText(serviceName)

        when (serviceName) {
            "Citas" -> {
                etQuantity.setText("15")
                etVeterinarians.setText("2")
                etPrice.setText("100")
                etDescription.setText("Consulta general para mascotas, incluye revisión y diagnóstico.")
            }
            "Cirugías" -> {
                etQuantity.setText("6")
                etVeterinarians.setText("1")
                etPrice.setText("21200")
                etDescription.setText("Procedimientos quirúrgicos con anestesia y seguimiento postoperatorio.")
            }
            "Observación" -> {
                etQuantity.setText("10")
                etVeterinarians.setText("1")
                etPrice.setText("600")
                etDescription.setText("Monitoreo y cuidado de pacientes hospitalizados.")
            }
            "Barro relajante" -> {
                etQuantity.setText("7")
                etVeterinarians.setText("1")
                etPrice.setText("2000")
                etDescription.setText("Terapia de relajación para mascotas con estrés o ansiedad.")
            }
            else -> {
                etQuantity.setText("0")
                etVeterinarians.setText("0")
                etPrice.setText("0")
                etDescription.setText("Descripción no disponible.")
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSaveToolbar.setOnClickListener {
            saveService()
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

        when {
            name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del servicio", Toast.LENGTH_SHORT).show()
            quantity.isEmpty() -> Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show()
            veterinarians.isEmpty() -> Toast.makeText(this, "Ingresa el número de veterinarios", Toast.LENGTH_SHORT).show()
            price.isEmpty() -> Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Servicio $name actualizado correctamente", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}