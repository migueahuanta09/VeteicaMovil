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
import com.example.veteica.R

class CreatePetActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: TextView
    private lateinit var ivPetPhoto: ImageView
    private lateinit var etName: EditText
    private lateinit var spinnerSpecies: Spinner
    private lateinit var etBreed: EditText
    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etColor: EditText
    private lateinit var etOwnerName: EditText
    private lateinit var etOwnerPhone: EditText
    private lateinit var etOwnerAddress: EditText
    private lateinit var etNotes: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pet)

        initViews()
        setupToolbar()
        setupSpinners()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etName = findViewById(R.id.etName)
        spinnerSpecies = findViewById(R.id.spinnerSpecies)
        etBreed = findViewById(R.id.etBreed)
        etAge = findViewById(R.id.etAge)
        etWeight = findViewById(R.id.etWeight)
        spinnerGender = findViewById(R.id.spinnerGender)
        etColor = findViewById(R.id.etColor)
        etOwnerName = findViewById(R.id.etOwnerName)
        etOwnerPhone = findViewById(R.id.etOwnerPhone)
        etOwnerAddress = findViewById(R.id.etOwnerAddress)
        etNotes = findViewById(R.id.etNotes)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinners() {
        // Especies
        val speciesList = arrayOf("Perro", "Gato", "Conejo", "Ave", "Reptil", "Otro")
        val speciesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speciesList)
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecies.adapter = speciesAdapter

        // Género
        val genderList = arrayOf("Macho", "Hembra")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        ivPetPhoto.setOnClickListener {
            Toast.makeText(this, "Agregar foto - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val species = spinnerSpecies.selectedItem.toString()
            val breed = etBreed.text.toString()
            val age = etAge.text.toString()
            val weight = etWeight.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val color = etColor.text.toString()
            val ownerName = etOwnerName.text.toString()
            val ownerPhone = etOwnerPhone.text.toString()
            val ownerAddress = etOwnerAddress.text.toString()
            val notes = etNotes.text.toString()

            when {
                name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre de la mascota", Toast.LENGTH_SHORT).show()
                breed.isEmpty() -> Toast.makeText(this, "Ingresa la raza", Toast.LENGTH_SHORT).show()
                age.isEmpty() -> Toast.makeText(this, "Ingresa la edad", Toast.LENGTH_SHORT).show()
                ownerName.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del dueño", Toast.LENGTH_SHORT).show()
                else -> {
                    Toast.makeText(this, "Paciente $name creado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}