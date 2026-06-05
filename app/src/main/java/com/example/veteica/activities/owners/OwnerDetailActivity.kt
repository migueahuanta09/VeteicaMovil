package com.example.veteica.activities.owners

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.OwnerAppointmentAdapter
import com.example.veteica.adapters.OwnerPetAdapter
import com.example.veteica.models.OwnerAppointment
import com.example.veteica.models.Pet
import com.example.veteica.network.RetrofitClient
import com.example.veteica.utils.PdfGenerator
import kotlinx.coroutines.launch

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

    private var ownerMongoId: String = ""
    private var ownerName: String = ""
    private var ownerPhone: String = ""
    private var ownerEmail: String = ""
    private var ownerAddress: String = ""
    private var petsListData = mutableListOf<Pair<String, String>>() // (nombre, especie)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_detail)

        initViews()
        setupToolbar()
        loadOwnerData()
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

    private fun loadOwnerData() {
        ownerMongoId = intent.getStringExtra("owner_mongo_id") ?: ""
        ownerName = intent.getStringExtra("owner_name") ?: ""
        ownerPhone = intent.getStringExtra("owner_phone") ?: ""
        ownerEmail = intent.getStringExtra("owner_email") ?: ""
        ownerAddress = intent.getStringExtra("owner_address") ?: ""

        val initial = if (ownerName.isNotEmpty()) ownerName[0].toString() else "?"
        tvOwnerPhoto.text = initial
        tvOwnerName.text = ownerName
        tvOwnerEmail.text = ownerEmail
        tvOwnerPhone.text = ownerPhone
        tvOwnerAddress.text = ownerAddress

        loadPetsFromBackend()
        loadAppointmentsFromBackend()
    }

    private fun loadPetsFromBackend() {
        if (ownerMongoId.isEmpty()) return
        val token = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
            .getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getOwner(ownerMongoId)

                if (response.isSuccessful) {
                    val data = response.body()?.get("data") as? Map<*, *>
                    val mascotasList = data?.get("mascotas") as? List<*>

                    petsListData.clear()
                    val petsList = mutableListOf<Pet>()

                    mascotasList?.forEachIndexed { index, item ->
                        val pet = item as? Map<*, *> ?: return@forEachIndexed
                        val petName = pet["nombre"] as? String ?: ""
                        val petSpecies = pet["especie"] as? String ?: ""

                        petsListData.add(Pair(petName, petSpecies))
                        petsList.add(Pet(
                            id = index + 1,
                            mongoId = pet["_id"] as? String ?: "",
                            name = petName,
                            species = petSpecies,
                            breed = "",
                            age = 0,
                            weight = 0.0,
                            gender = "",
                            color = "",
                            ownerName = ownerName,
                            notes = ""
                        ))
                    }

                    rvPets.layoutManager = LinearLayoutManager(this@OwnerDetailActivity)
                    rvPets.adapter = OwnerPetAdapter(petsList) { pet ->
                        Toast.makeText(this@OwnerDetailActivity, "Mascota: ${pet.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@OwnerDetailActivity, "Error cargando mascotas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAppointmentsFromBackend() {
        // Por ahora mock, después conectamos
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

    override fun onResume() {
        super.onResume()
        loadPetsFromBackend()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditOwnerActivity::class.java)
            intent.putExtra("owner_mongo_id", ownerMongoId)
            intent.putExtra("owner_name", ownerName)
            intent.putExtra("owner_phone", ownerPhone)
            intent.putExtra("owner_email", ownerEmail)
            intent.putExtra("owner_address", ownerAddress)
            startActivity(intent)
        }

        btnDownloadFicha.setOnClickListener {
            if (petsListData.isEmpty()) {
                Toast.makeText(this, "No hay mascotas asociadas para generar la ficha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PdfGenerator.generateOwnerFicha(
                this,
                ownerName,
                ownerPhone,
                ownerEmail,
                ownerAddress,
                petsListData
            )
        }

        btnGenerarCarnet.setOnClickListener {
            if (petsListData.isEmpty()) {
                Toast.makeText(this, "No hay mascotas asociadas para generar el carnet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar diálogo para seleccionar qué mascota
            val petNames = petsListData.map { it.first }.toTypedArray()
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar mascota")
                .setItems(petNames) { _, which ->
                    val selectedPet = petsListData[which]
                    // Por ahora usamos datos mock, después se pueden obtener del backend
                    PdfGenerator.generateCarnetVeterinario(
                        this,
                        ownerName,
                        selectedPet.first,
                        selectedPet.second,
                        "Raza no especificada",
                        "Color no especificado"
                    )
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}