package com.example.veteica.activities.payments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

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
    private lateinit var prefs: SharedPreferences

    private var serviceMongoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
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
        serviceMongoId = intent.getStringExtra("service_mongo_id") ?: ""
        etServiceName.setText(intent.getStringExtra("service_name") ?: "")
        etQuantity.setText(intent.getIntExtra("service_quantity", 0).toString())
        etVeterinarians.setText(intent.getIntExtra("service_vets", 0).toString())
        etPrice.setText(intent.getDoubleExtra("service_price", 0.0).toString())
        etDescription.setText(intent.getStringExtra("service_description") ?: "")
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }
        btnSaveToolbar.setOnClickListener { saveService() }
        btnSave.setOnClickListener { saveService() }
    }

    private fun saveService() {
        val name = etServiceName.text.toString().trim()
        val quantity = etQuantity.text.toString().trim()
        val veterinarians = etVeterinarians.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val description = etDescription.text.toString().trim()

        when {
            name.isEmpty() -> { Toast.makeText(this, "Ingresa el nombre del servicio", Toast.LENGTH_SHORT).show(); return }
            quantity.isEmpty() -> { Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show(); return }
            veterinarians.isEmpty() -> { Toast.makeText(this, "Ingresa el número de veterinarios", Toast.LENGTH_SHORT).show(); return }
            price.isEmpty() -> { Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show(); return }
        }

        if (serviceMongoId.isEmpty()) {
            Toast.makeText(this, "Error: ID de servicio no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val token = prefs.getString("token", "") ?: ""
        btnSave.isEnabled = false

        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "nombre" to name,
                    "cantidad" to quantity,
                    "veterinarios" to veterinarians,
                    "precio" to price,
                    "descripcion" to description
                )
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.updateService(serviceMongoId, body)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditServiceActivity, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditServiceActivity, "Error al actualizar servicio", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditServiceActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnSave.isEnabled = true
            }
        }
    }
}