package com.example.veteica.activities.pets

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R
import com.example.veteica.databinding.ActivityCreatePetBinding

class CreatePetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nuevo Paciente"
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            val name = binding.etPetName.text.toString()
            val species = binding.etSpecies.text.toString()
            val breed = binding.etBreed.text.toString()
            val age = binding.etAge.text.toString()
            val weight = binding.etWeight.text.toString()
            val gender = binding.etGender.text.toString()
            val color = binding.etColor.text.toString()
            val owner = binding.etOwnerName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre de la mascota", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Paciente $name creado exitosamente", Toast.LENGTH_SHORT).show()
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