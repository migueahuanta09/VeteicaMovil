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
        val token = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
            .getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getPets()

                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    val items = data?.get("items") as? List<*>

                    val petsList = mutableListOf<Pet>()
                    items?.forEachIndexed { index, item ->
                        val pet = item as? Map<*, *> ?: return@forEachIndexed
                        val dueno = pet["nombreDueno"] as? String ?: ""
                        if (dueno.equals(ownerName, ignoreCase = true)) {
                            petsList.add(Pet(
                                id = index + 1,
                                mongoId = pet["_id"] as? String ?: "",
                                name = pet["nombre"] as? String ?: "",
                                species = pet["especie"] as? String ?: "",
                                breed = pet["raza"] as? String ?: "",
                                age = (pet["edad"] as? Double)?.toInt() ?: 0,
                                weight = pet["peso"] as? Double ?: 0.0,
                                gender = pet["genero"] as? String ?: "",
                                color = pet["color"] as? String ?: "",
                                ownerName = dueno,
                                notes = pet["notas"] as? String ?: ""
                            ))
                        }
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
            Toast.makeText(this, "Descargando ficha PDF...", Toast.LENGTH_SHORT).show()
        }

        btnGenerarCarnet.setOnClickListener {
            Toast.makeText(this, "Generando carnet veterinario PDF...", Toast.LENGTH_SHORT).show()
        }
    }
}