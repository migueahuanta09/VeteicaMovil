package com.example.veteica.activities.owners

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.OwnerPetAdapter
import com.example.veteica.adapters.OwnerAppointmentAdapter
import com.example.veteica.models.Pet
import com.example.veteica.models.OwnerAppointment

class OwnerDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: TextView
    private lateinit var tvOwnerPhoto: TextView
    private lateinit var tvOwnerName: TextView
    private lateinit var tvOwnerEmail: TextView
    private lateinit var tvOwnerPhone: TextView
    private lateinit var tvOwnerAddress: TextView
    private lateinit var rvPets: RecyclerView
    private lateinit var rvAppointments: RecyclerView
    private lateinit var btnDownloadFicha: com.google.android.material.button.MaterialButton
    private lateinit var btnGenerarCarnet: com.google.android.material.button.MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_detail)

        initViews()
        setupToolbar()
        loadMockData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        tvOwnerPhoto = findViewById(R.id.tvOwnerPhoto)
        tvOwnerName = findViewById(R.id.tvOwnerName)
        tvOwnerEmail = findViewById(R.id.tvOwnerEmail)
        tvOwnerPhone = findViewById(R.id.tvOwnerPhone)
        tvOwnerAddress = findViewById(R.id.tvOwnerAddress)
        rvPets = findViewById(R.id.rvPets)
        rvAppointments = findViewById(R.id.rvAppointments)
        btnDownloadFicha = findViewById(R.id.btnDownloadFicha)
        btnGenerarCarnet = findViewById(R.id.btnGenerarCarnet)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadMockData() {
        val ownerId = intent.getIntExtra("owner_id", 1)
        val ownerName = intent.getStringExtra("owner_name") ?: "Jose Herrera"

        // Inicial de la foto (primera letra del nombre)
        val initial = if (ownerName.isNotEmpty()) ownerName[0].toString() else "X"

        tvOwnerPhoto.text = initial
        tvOwnerName.text = ownerName
        tvOwnerEmail.text = "jose@gmail.com"
        tvOwnerPhone.text = "612-123-4567"
        tvOwnerAddress.text = "Colonia Púrpura. calle 123"

        // Mascotas asociadas
        val petsList = listOf(
            Pet(1, "Lilo", "Perro", "Labrador", 3, 28.5, "Macho", "Dorado", ownerName, ""),
            Pet(2, "Max", "Perro", "Bulldog", 5, 32.0, "Macho", "Atigrado", ownerName, "")
        )

        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = OwnerPetAdapter(petsList) { pet ->
            Toast.makeText(this, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
        }

        // Historial de citas
        val appointmentsList = listOf(
            OwnerAppointment(1, "Consulta general", "22/10/2025", "Ligero problema en el oído.", "Navarro admin"),
            OwnerAppointment(2, "Revisión", "23/10/2025", "Aun con ligero problema en el oído.", "Navarro admin"),
            OwnerAppointment(3, "Tratamiento", "24/10/2025", "Infección de oído", "Navarro admin")
        )

        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = OwnerAppointmentAdapter(appointmentsList) { appointment ->
            Toast.makeText(this, "Cita: ${appointment.consulta}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditOwnerActivity::class.java)
            intent.putExtra("owner_id", intent.getIntExtra("owner_id", 1))
            intent.putExtra("owner_name", tvOwnerName.text.toString())
            startActivity(intent)
        }

        btnDownloadFicha.setOnClickListener {
            Toast.makeText(this, "Descargando ficha PDF...", Toast.LENGTH_SHORT).show()
        }

        btnGenerarCarnet.setOnClickListener {
            Toast.makeText(this, "Generando carnet veterinario PDF...", Toast.LENGTH_SHORT).show()
        }
    }
}