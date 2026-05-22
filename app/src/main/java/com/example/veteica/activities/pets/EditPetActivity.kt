package com.example.veteica.activities.pets

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.HistorialAdapter
import com.example.veteica.adapters.VacunaAdapter
import com.example.veteica.models.Historial
import com.example.veteica.models.Vacuna

class EditPetActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: TextView
    private lateinit var ivPetPhoto: ImageView
    private lateinit var etSpecies: EditText
    private lateinit var etBreed: EditText
    private lateinit var etAge: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etOwnerName: EditText
    private lateinit var etOwnerPhone: EditText
    private lateinit var etOwnerAddress: EditText
    private lateinit var rvHistorialClinico: RecyclerView
    private lateinit var rvVacunas: RecyclerView
    private var petId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)

        initViews()
        setupToolbar()
        setupSpinner()
        loadMockData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etSpecies = findViewById(R.id.etSpecies)
        etBreed = findViewById(R.id.etBreed)
        etAge = findViewById(R.id.etAge)
        spinnerGender = findViewById(R.id.spinnerGender)
        etOwnerName = findViewById(R.id.etOwnerName)
        etOwnerPhone = findViewById(R.id.etOwnerPhone)
        etOwnerAddress = findViewById(R.id.etOwnerAddress)
        rvHistorialClinico = findViewById(R.id.rvHistorialClinico)
        rvVacunas = findViewById(R.id.rvVacunas)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinner() {
        val genders = arrayOf("Macho", "Hembra")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
    }

    private fun loadMockData() {
        petId = intent.getIntExtra("pet_id", 1)
        val petName = intent.getStringExtra("pet_name") ?: "Lilo"

        supportActionBar?.title = "Editar $petName"

        // Cargar datos existentes
        etSpecies.setText("Perro")
        etBreed.setText("Labrador Retriever")
        etAge.setText("3")
        spinnerGender.setSelection(0) // Macho
        etOwnerName.setText("Juan Pérez")
        etOwnerPhone.setText("555-1234-567")
        etOwnerAddress.setText("Av. Principal #123, Col. Centro")

        // Historial Clínico (solo vista)
        val historialList = listOf(
            Historial(1, "Consulta general", "15/01/2025", "Infección respiratoria", "Dra. María González"),
            Historial(2, "Vacunación", "10/01/2025", "Vacuna antirrábica", "Dr. Carlos López"),
            Historial(3, "Revisión", "05/01/2025", "Control de peso", "Dra. Ana Martínez")
        )

        rvHistorialClinico.layoutManager = LinearLayoutManager(this)
        rvHistorialClinico.adapter = HistorialAdapter(historialList) { historial ->
            Toast.makeText(this, "Consulta: ${historial.consulta}", Toast.LENGTH_SHORT).show()
        }

        // Historial Vacunas (solo vista)
        val vacunasList = listOf(
            Vacuna(1, "Rabia", "1 dosis", "10/01/2025"),
            Vacuna(2, "Parvovirus", "3 dosis", "05/01/2025"),
            Vacuna(3, "Moquillo", "2 dosis", "01/01/2025")
        )

        rvVacunas.layoutManager = LinearLayoutManager(this)
        rvVacunas.adapter = VacunaAdapter(vacunasList) { vacuna ->
            Toast.makeText(this, "Vacuna: ${vacuna.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        ivPetPhoto.setOnClickListener {
            Toast.makeText(this, "Cambiar foto - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            val species = etSpecies.text.toString()
            val breed = etBreed.text.toString()
            val age = etAge.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val ownerName = etOwnerName.text.toString()
            val ownerPhone = etOwnerPhone.text.toString()
            val ownerAddress = etOwnerAddress.text.toString()

            if (species.isEmpty() || breed.isEmpty() || age.isEmpty() || ownerName.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Paciente actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}