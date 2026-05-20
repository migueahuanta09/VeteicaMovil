package com.example.veteica.activities.pets

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R
import com.example.veteica.databinding.ActivityCreatePetBinding

class EditPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePetBinding
    private var petId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        petId = intent.getIntExtra("pet_id", 0)

        setupToolbar()
        loadMockData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Editar Paciente"
    }

    private fun loadMockData() {
        binding.etPetName.setText("Max")
        binding.etSpecies.setText("Perro")
        binding.etBreed.setText("Labrador")
        binding.etAge.setText("3")
        binding.etWeight.setText("28.5")
        binding.etGender.setText("Macho")
        binding.etColor.setText("Dorado")
        binding.etOwnerName.setText("Juan Pérez")
        binding.etNotes.setText("Paciente activo, sin alergias")
    }

    private fun setupClickListeners() {
        binding.btnSave.text = "ACTUALIZAR"
        binding.btnSave.setOnClickListener {
            Toast.makeText(this, "Paciente actualizado", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}