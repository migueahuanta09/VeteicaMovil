package com.example.veteica.activities.appointments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CreateAppointmentActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnCreate: MaterialButton
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etVeterinarian: EditText
    private lateinit var etReason: EditText
    private lateinit var etDiagnosis: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerPet: Spinner
    private lateinit var tvOwnerName: TextView
    private lateinit var prefs: SharedPreferences

    // Guardamos los datos de las mascotas para sacar el dueño
    private val petNames = mutableListOf<String>()
    private val petOwners = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)

        initViews()
        setupToolbar()
        setupSpinners()
        setupClickListeners()
        loadPets()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCreate = findViewById(R.id.btnCreate)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etVeterinarian = findViewById(R.id.etVeterinarian)
        etReason = findViewById(R.id.etReason)
        etDiagnosis = findViewById(R.id.etDiagnosis)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        spinnerPet = findViewById(R.id.spinnerPet)
        tvOwnerName = findViewById(R.id.tvOwnerName)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinners() {
        val statusList = arrayOf("Pendiente", "Confirmada", "Completada", "Cancelada")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter
        spinnerStatus.setSelection(0)
    }

    private fun loadPets() {
        val token = prefs.getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getPets()

                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    val items = data?.get("items") as? List<*>

                    petNames.clear()
                    petOwners.clear()

                    items?.forEach { item ->
                        val pet = item as? Map<*, *>
                        val nombre = pet?.get("nombre") as? String ?: ""
                        val dueno = pet?.get("nombreDueno") as? String ?: ""
                        if (nombre.isNotEmpty()) {
                            petNames.add(nombre)
                            petOwners.add(dueno)
                        }
                    }

                    val petAdapter = ArrayAdapter(
                        this@CreateAppointmentActivity,
                        android.R.layout.simple_spinner_item,
                        petNames
                    )
                    petAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerPet.adapter = petAdapter

                    // Cuando cambia la mascota, actualizar el dueño
                    spinnerPet.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                            tvOwnerName.text = petOwners.getOrElse(position) { "Sin dueño" }
                        }
                        override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                    }

                    if (petNames.isEmpty()) {
                        Toast.makeText(this@CreateAppointmentActivity, "No hay mascotas registradas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateAppointmentActivity, "Error cargando mascotas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        btnCreate.setOnClickListener { createAppointment() }
    }

    private fun createAppointment() {
        val date = etDate.text.toString().trim()
        val time = etTime.text.toString().trim()
        val veterinarian = etVeterinarian.text.toString().trim()
        val reason = etReason.text.toString().trim()
        val status = spinnerStatus.selectedItem.toString()
        val petName = spinnerPet.selectedItem as? String ?: ""
        val ownerName = tvOwnerName.text.toString()
        val diagnosis = etDiagnosis.text.toString().trim()

        when {
            petName.isEmpty() -> { Toast.makeText(this, "Selecciona una mascota", Toast.LENGTH_SHORT).show(); return }
            date.isEmpty() -> { Toast.makeText(this, "Ingresa la fecha", Toast.LENGTH_SHORT).show(); return }
            time.isEmpty() -> { Toast.makeText(this, "Ingresa la hora", Toast.LENGTH_SHORT).show(); return }
            veterinarian.isEmpty() -> { Toast.makeText(this, "Ingresa el veterinario", Toast.LENGTH_SHORT).show(); return }
            reason.isEmpty() -> { Toast.makeText(this, "Ingresa el motivo", Toast.LENGTH_SHORT).show(); return }
        }

        val token = prefs.getString("token", "") ?: ""
        btnCreate.isEnabled = false
        btnCreate.text = "Guardando..."

        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "nombreMascota" to petName,
                    "nombreDueno" to ownerName,
                    "fecha" to date,
                    "hora" to time,
                    "veterinario" to veterinarian,
                    "motivo" to reason,
                    "diagnostico" to diagnosis,
                    "estado" to status
                )
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.createAppointment(body)

                if (response.isSuccessful) {
                    Toast.makeText(this@CreateAppointmentActivity, "Cita creada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateAppointmentActivity, "Error al crear cita", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateAppointmentActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnCreate.isEnabled = true
                btnCreate.text = "CREAR CITA"
            }
        }
    }
}