package com.example.veteica.activities.pets

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
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

class PetDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: TextView
    private lateinit var tvPetName: TextView
    private lateinit var tvSpecies: TextView
    private lateinit var tvBreed: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvOwnerName: TextView
    private lateinit var tvOwnerPhone: TextView
    private lateinit var tvOwnerAddress: TextView
    private lateinit var rvHistorialClinico: RecyclerView
    private lateinit var rvVacunas: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)

        initViews()
        setupToolbar()
        loadMockData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        tvPetName = findViewById(R.id.tvPetName)
        tvSpecies = findViewById(R.id.tvSpecies)
        tvBreed = findViewById(R.id.tvBreed)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvOwnerName = findViewById(R.id.tvOwnerName)
        tvOwnerPhone = findViewById(R.id.tvOwnerPhone)
        tvOwnerAddress = findViewById(R.id.tvOwnerAddress)
        rvHistorialClinico = findViewById(R.id.rvHistorialClinico)
        rvVacunas = findViewById(R.id.rvVacunas)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadMockData() {
        // Datos de la mascota
        val petId = intent.getIntExtra("pet_id", 1)
        val petName = intent.getStringExtra("pet_name") ?: "Lilo"

        tvPetName.text = petName
        tvSpecies.text = "Perro"
        tvBreed.text = "Labrador Retriever"
        tvAge.text = "3 años"
        tvGender.text = "Macho"
        tvOwnerName.text = "Juan Pérez"
        tvOwnerPhone.text = "555-1234-567"
        tvOwnerAddress.text = "Av. Principal #123, Col. Centro"

        // Historial Clínico
        val historialList = listOf(
            Historial(1, "Consulta general", "15/01/2025", "Infección respiratoria", "Dra. María González"),
            Historial(2, "Vacunación", "10/01/2025", "Vacuna antirrábica", "Dr. Carlos López"),
            Historial(3, "Revisión", "05/01/2025", "Control de peso", "Dra. Ana Martínez"),
            Historial(4, "Emergencia", "28/12/2024", "Herida en pata", "Dr. Luis Rodríguez"),
            Historial(5, "Consulta general", "20/12/2024", "Chequeo anual", "Dra. María González")
        )

        rvHistorialClinico.layoutManager = LinearLayoutManager(this)
        rvHistorialClinico.adapter = HistorialAdapter(historialList) { historial ->
            Toast.makeText(this, "Consulta: ${historial.consulta}", Toast.LENGTH_SHORT).show()
        }

        // Historial Vacunas
        val vacunasList = listOf(
            Vacuna(1, "Rabia", "1 dosis", "10/01/2025"),
            Vacuna(2, "Parvovirus", "3 dosis", "05/01/2025"),
            Vacuna(3, "Moquillo", "2 dosis", "01/01/2025"),
            Vacuna(4, "Hepatitis", "1 dosis", "28/12/2024"),
            Vacuna(5, "Leptospirosis", "2 dosis", "20/12/2024")
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

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditPetActivity::class.java)
            intent.putExtra("pet_id", intent.getIntExtra("pet_id", 1))
            intent.putExtra("pet_name", tvPetName.text.toString())
            startActivity(intent)
        }
    }
}