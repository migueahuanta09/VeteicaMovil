package com.example.veteica.activities.appointments

import android.net.Uri
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
import com.example.veteica.models.Pet
import java.io.File

class CreateAppointmentActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnCreate: com.google.android.material.button.MaterialButton
    private lateinit var ivPetPhoto: ImageView
    private lateinit var spinnerPet: Spinner
    private lateinit var tvOwnerName: TextView
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etVeterinarian: EditText
    private lateinit var etReason: EditText
    private lateinit var etDiagnosis: EditText
    private lateinit var spinnerStatus: Spinner

    private val petsList = mutableListOf<Pet>()
    private var selectedPet: Pet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        initViews()
        setupToolbar()
        setupSpinners()
        loadMockPets()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCreate = findViewById(R.id.btnCreate)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        spinnerPet = findViewById(R.id.spinnerPet)
        tvOwnerName = findViewById(R.id.tvOwnerName)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etVeterinarian = findViewById(R.id.etVeterinarian)
        etReason = findViewById(R.id.etReason)
        etDiagnosis = findViewById(R.id.etDiagnosis)
        spinnerStatus = findViewById(R.id.spinnerStatus)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinners() {
        // Spinner de estados
        val statusList = arrayOf("Pendiente", "Confirmada", "Revisado", "Completada", "Cancelada")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter
        spinnerStatus.setSelection(0) // Pendiente por defecto
    }

    private fun loadMockPets() {
        // Datos mock de mascotas
        petsList.addAll(listOf(
            Pet(1, "Lilo", "Perro", "Labrador", 3, 28.5, "Macho", "Dorado", "José Herrera", "", null),
            Pet(2, "Max", "Perro", "Bulldog", 5, 32.0, "Macho", "Atigrado", "Juan Pérez", "", null),
            Pet(3, "Luna", "Gato", "Siames", 2, 4.2, "Hembra", "Blanco", "María García", "", null),
            Pet(4, "Rocky", "Perro", "Pastor Alemán", 4, 35.0, "Macho", "Negro", "Carlos López", "", null),
            Pet(5, "Bella", "Perro", "Poodle", 1, 6.5, "Hembra", "Blanco", "Ana Martínez", "", null)
        ))

        // Configurar spinner de mascotas
        val petNames = petsList.map { "${it.name} (${it.species})" }
        val petAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petNames)
        petAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPet.adapter = petAdapter

        // Seleccionar primera mascota por defecto
        spinnerPet.setSelection(0)
        updateSelectedPet(0)

        // Listener para cambio de mascota
        spinnerPet.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateSelectedPet(position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun updateSelectedPet(position: Int) {
        if (position < petsList.size) {
            selectedPet = petsList[position]
            selectedPet?.let { pet ->
                tvOwnerName.text = pet.ownerName
                loadPetPhoto(pet.photoUri)
            }
        }
    }

    private fun loadPetPhoto(photoUri: String?) {
        if (!photoUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(photoUri)
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    ivPetPhoto.setImageURI(uri)
                    ivPetPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPetPhoto.setPadding(0, 0, 0, 0)
                    ivPetPhoto.setColorFilter(null)
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Foto por defecto
        ivPetPhoto.setImageResource(R.drawable.ic_pet)
        ivPetPhoto.setColorFilter(resources.getColor(R.color.veteica_green))
        ivPetPhoto.setPadding(20, 20, 20, 20)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCreate.setOnClickListener {
            createAppointment()
        }
    }

    private fun createAppointment() {
        val selectedPosition = spinnerPet.selectedItemPosition
        val pet = if (selectedPosition < petsList.size) petsList[selectedPosition] else null

        val petName = pet?.name ?: ""
        val ownerName = tvOwnerName.text.toString()
        val date = etDate.text.toString().trim()
        val time = etTime.text.toString().trim()
        val veterinarian = etVeterinarian.text.toString().trim()
        val reason = etReason.text.toString().trim()
        val diagnosis = etDiagnosis.text.toString().trim()
        val status = spinnerStatus.selectedItem.toString()

        when {
            pet == null -> Toast.makeText(this, "Selecciona una mascota", Toast.LENGTH_SHORT).show()
            date.isEmpty() -> Toast.makeText(this, "Ingresa la fecha", Toast.LENGTH_SHORT).show()
            time.isEmpty() -> Toast.makeText(this, "Ingresa la hora", Toast.LENGTH_SHORT).show()
            veterinarian.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del veterinario", Toast.LENGTH_SHORT).show()
            reason.isEmpty() -> Toast.makeText(this, "Ingresa el motivo", Toast.LENGTH_SHORT).show()
            else -> {
                val message = "Cita creada correctamente\n$petName - $date $time\nEstado: $status"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}