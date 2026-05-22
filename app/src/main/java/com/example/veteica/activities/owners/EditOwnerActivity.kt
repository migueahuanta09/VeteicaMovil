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
import com.example.veteica.adapters.OwnerPetAdapter
import com.example.veteica.adapters.OwnerAppointmentAdapter
import com.example.veteica.models.Pet
import com.example.veteica.models.OwnerAppointment

class EditOwnerActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: com.google.android.material.button.MaterialButton
    private lateinit var btnCancel: com.google.android.material.button.MaterialButton
    private lateinit var tvOwnerPhoto: TextView
    private lateinit var etOwnerName: EditText
    private lateinit var etOwnerEmail: EditText
    private lateinit var etOwnerPhone: EditText
    private lateinit var etOwnerAddress: EditText
    private lateinit var rvPets: RecyclerView
    private lateinit var rvAppointments: RecyclerView
    private lateinit var layoutPhoto: android.widget.LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_owner)

        initViews()
        setupToolbar()
        loadExistingData()
        setupClickListeners()
        setupReadOnlyData()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        tvOwnerPhoto = findViewById(R.id.tvOwnerPhoto)
        etOwnerName = findViewById(R.id.etOwnerName)
        etOwnerEmail = findViewById(R.id.etOwnerEmail)
        etOwnerPhone = findViewById(R.id.etOwnerPhone)
        etOwnerAddress = findViewById(R.id.etOwnerAddress)
        rvPets = findViewById(R.id.rvPets)
        rvAppointments = findViewById(R.id.rvAppointments)
        layoutPhoto = findViewById(R.id.layoutPhoto)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadExistingData() {
        val ownerId = intent.getIntExtra("owner_id", 1)
        val ownerName = intent.getStringExtra("owner_name") ?: "Jose Herrera"

        val initial = if (ownerName.isNotEmpty()) ownerName[0].toString() else "J"
        tvOwnerPhoto.text = initial
        etOwnerName.setText(ownerName)
        etOwnerEmail.setText("jose@gmail.com")
        etOwnerPhone.setText("612-123-4567")
        etOwnerAddress.setText("Colonia Púrpura. calle 123")
    }

    private fun setupReadOnlyData() {
        val ownerName = etOwnerName.text.toString()
        val petsList = listOf(
            Pet(1, "Lilo", "Perro", "Labrador", 3, 28.5, "Macho", "Dorado", ownerName, ""),
            Pet(2, "Max", "Perro", "Bulldog", 5, 32.0, "Macho", "Atigrado", ownerName, "")
        )

        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = OwnerPetAdapter(petsList) { pet ->
            Toast.makeText(this, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
        }

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

        btnCancel.setOnClickListener {
            finish()
        }

        layoutPhoto.setOnClickListener {
            Toast.makeText(this, "Cambiar foto - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener {
            val name = etOwnerName.text.toString()
            val email = etOwnerEmail.text.toString()
            val phone = etOwnerPhone.text.toString()
            val address = etOwnerAddress.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre del dueño", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Dueño $name actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}