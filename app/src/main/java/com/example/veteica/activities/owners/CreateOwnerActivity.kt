package com.example.veteica.activities.owners

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.SimplePetAdapter
import com.example.veteica.models.SimplePet

class CreateOwnerActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnCreate: com.google.android.material.button.MaterialButton
    private lateinit var btnCancel: com.google.android.material.button.MaterialButton
    private lateinit var btnAddPet: com.google.android.material.button.MaterialButton
    private lateinit var etOwnerName: EditText
    private lateinit var etOwnerPhone: EditText
    private lateinit var etOwnerEmail: EditText
    private lateinit var etOwnerAddress: EditText
    private lateinit var etPetName: EditText
    private lateinit var rvPets: RecyclerView
    private lateinit var layoutPhoto: android.widget.LinearLayout
    private lateinit var tvAddPhoto: TextView

    private val petsList = mutableListOf<SimplePet>()
    private lateinit var petAdapter: SimplePetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_owner)

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCreate = findViewById(R.id.btnCreate)
        btnCancel = findViewById(R.id.btnCancel)
        btnAddPet = findViewById(R.id.btnAddPet)
        etOwnerName = findViewById(R.id.etOwnerName)
        etOwnerPhone = findViewById(R.id.etOwnerPhone)
        etOwnerEmail = findViewById(R.id.etOwnerEmail)
        etOwnerAddress = findViewById(R.id.etOwnerAddress)
        etPetName = findViewById(R.id.etPetName)
        rvPets = findViewById(R.id.rvPets)
        layoutPhoto = findViewById(R.id.layoutPhoto)
        tvAddPhoto = findViewById(R.id.tvAddPhoto)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        petAdapter = SimplePetAdapter(petsList) { pet, position ->
            petsList.removeAt(position)
            petAdapter.updateList(petsList)
            Toast.makeText(this, "Mascota ${pet.name} eliminada", Toast.LENGTH_SHORT).show()
        }
        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = petAdapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        layoutPhoto.setOnClickListener {
            Toast.makeText(this, "Agregar imagen - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnAddPet.setOnClickListener {
            val petName = etPetName.text.toString().trim()
            if (petName.isNotEmpty()) {
                petsList.add(SimplePet(petName))
                petAdapter.updateList(petsList)
                etPetName.text.clear()
                Toast.makeText(this, "Mascota $petName agregada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ingresa el nombre de la mascota", Toast.LENGTH_SHORT).show()
            }
        }

        btnCreate.setOnClickListener {
            val name = etOwnerName.text.toString().trim()
            val phone = etOwnerPhone.text.toString().trim()
            val email = etOwnerEmail.text.toString().trim()
            val address = etOwnerAddress.text.toString().trim()

            when {
                name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del dueño", Toast.LENGTH_SHORT).show()
                phone.isEmpty() -> Toast.makeText(this, "Ingresa el teléfono", Toast.LENGTH_SHORT).show()
                email.isEmpty() -> Toast.makeText(this, "Ingresa el correo electrónico", Toast.LENGTH_SHORT).show()
                address.isEmpty() -> Toast.makeText(this, "Ingresa la dirección", Toast.LENGTH_SHORT).show()
                else -> {
                    val petsCount = petsList.size
                    Toast.makeText(this, "Dueño $name creado con $petsCount mascota(s)", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}