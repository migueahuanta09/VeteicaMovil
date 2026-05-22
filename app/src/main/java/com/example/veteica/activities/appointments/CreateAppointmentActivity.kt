package com.example.veteica.activities.appointments

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class CreateAppointmentActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnSave: com.google.android.material.button.MaterialButton
    private lateinit var btnCancel: com.google.android.material.button.MaterialButton
    private lateinit var ivPetPhoto: ImageView
    private lateinit var etPetName: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etOwnerName: EditText
    private lateinit var etVeterinarian: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var etReason: EditText
    private lateinit var etDiagnosis: EditText
    private lateinit var layoutPhoto: android.widget.LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        initViews()
        setupToolbar()
        setupSpinner()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etPetName = findViewById(R.id.etPetName)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etOwnerName = findViewById(R.id.etOwnerName)
        etVeterinarian = findViewById(R.id.etVeterinarian)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        etReason = findViewById(R.id.etReason)
        etDiagnosis = findViewById(R.id.etDiagnosis)
        layoutPhoto = findViewById(R.id.layoutPhoto)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinner() {
        val statusList = arrayOf("Pendiente", "Confirmada", "Revisado", "Completada", "Cancelada")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
        // Por defecto seleccionar "Pendiente"
        spinnerStatus.setSelection(0)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSaveToolbar.setOnClickListener {
            saveAppointment()
        }

        btnSave.setOnClickListener {
            saveAppointment()
        }

        layoutPhoto.setOnClickListener {
            Toast.makeText(this, "Agregar foto - Próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAppointment() {
        val petName = etPetName.text.toString().trim()
        val date = etDate.text.toString().trim()
        val time = etTime.text.toString().trim()
        val ownerName = etOwnerName.text.toString().trim()
        val veterinarian = etVeterinarian.text.toString().trim()
        val status = spinnerStatus.selectedItem.toString()
        val reason = etReason.text.toString().trim()
        val diagnosis = etDiagnosis.text.toString().trim()

        when {
            petName.isEmpty() -> Toast.makeText(this, "Ingresa el nombre de la mascota", Toast.LENGTH_SHORT).show()
            date.isEmpty() -> Toast.makeText(this, "Ingresa la fecha", Toast.LENGTH_SHORT).show()
            time.isEmpty() -> Toast.makeText(this, "Ingresa la hora", Toast.LENGTH_SHORT).show()
            ownerName.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del dueño", Toast.LENGTH_SHORT).show()
            veterinarian.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del veterinario", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Cita creada correctamente\n$petName - $date $time\nEstado: $status", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}