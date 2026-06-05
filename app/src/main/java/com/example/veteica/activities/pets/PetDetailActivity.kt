package com.example.veteica.activities.pets

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.HistorialAdapter
import com.example.veteica.adapters.VacunaAdapter
import com.example.veteica.models.Historial
import com.example.veteica.models.Vacuna
import com.example.veteica.network.RetrofitClient
import com.example.veteica.utils.PdfGeneratorPet
import kotlinx.coroutines.launch

class PetDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: TextView
    private lateinit var btnExportPdf: com.google.android.material.button.MaterialButton
    private lateinit var ivPetPhoto: ImageView
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
    private lateinit var prefs: SharedPreferences

    private var petMongoId: String = ""
    private var petName: String = ""
    private var petSpecies: String = ""
    private var petBreed: String = ""
    private var petAge: Int = 0
    private var petGender: String = ""
    private var petOwnerName: String = ""
    private var petWeight: Double = 0.0
    private var petColor: String = ""
    private var petNotes: String = ""

    private var historialList = mutableListOf<Historial>()
    private var vacunasList = mutableListOf<Vacuna>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        loadPetData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        btnExportPdf = findViewById(R.id.btnExportPdf)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
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

    private fun loadPetData() {
        petMongoId = intent.getStringExtra("pet_mongo_id") ?: ""
        petName = intent.getStringExtra("pet_name") ?: ""

        tvPetName.text = petName

        if (petMongoId.isNotEmpty()) {
            loadPetFromApi()
            loadHistorial()
            loadVacunas()
        } else {
            tvSpecies.text = intent.getStringExtra("pet_species") ?: "No especificado"
            tvBreed.text = intent.getStringExtra("pet_breed") ?: "No especificado"
            tvAge.text = "${intent.getIntExtra("pet_age", 0)} años"
            tvGender.text = intent.getStringExtra("pet_gender") ?: "No especificado"
            tvOwnerName.text = intent.getStringExtra("pet_owner_name") ?: "No especificado"

            petSpecies = tvSpecies.text.toString()
            petBreed = tvBreed.text.toString()
            petAge = intent.getIntExtra("pet_age", 0)
            petGender = tvGender.text.toString()
            petOwnerName = tvOwnerName.text.toString()
            petWeight = intent.getDoubleExtra("pet_weight", 0.0)
            petColor = intent.getStringExtra("pet_color") ?: ""
            petNotes = intent.getStringExtra("pet_notes") ?: ""
        }
    }

    private fun loadPetFromApi() {
        val token = prefs.getString("token", "") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getPet(petMongoId)

                if (response.isSuccessful) {
                    val body = response.body()
                    val success = body?.get("success") as? Boolean ?: false

                    if (success) {
                        val data = body?.get("data") as? Map<*, *>
                        if (data != null) {
                            petName = data["nombre"] as? String ?: petName
                            petSpecies = data["especie"] as? String ?: "No especificado"
                            petBreed = data["raza"] as? String ?: "No especificado"
                            petAge = (data["edad"] as? Number)?.toInt() ?: 0
                            petGender = data["genero"] as? String ?: "No especificado"
                            petOwnerName = data["nombreDueno"] as? String ?: "No especificado"
                            petWeight = (data["peso"] as? Number)?.toDouble() ?: 0.0
                            petColor = data["color"] as? String ?: ""
                            petNotes = data["notas"] as? String ?: ""

                            tvPetName.text = petName
                            tvSpecies.text = petSpecies
                            tvBreed.text = petBreed
                            tvAge.text = "$petAge años"
                            tvGender.text = petGender
                            tvOwnerName.text = petOwnerName
                        }
                    } else {
                        val message = body?.get("message") as? String ?: "Error al cargar datos"
                        Toast.makeText(this@PetDetailActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PetDetailActivity, "Error de conexión: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PetDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadHistorial() {
        val token = prefs.getString("token", "") ?: ""
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getMedicalHistory(petMongoId)
                if (response.isSuccessful) {
                    val body = response.body()
                    val success = body?.get("success") as? Boolean ?: false
                    if (success) {
                        val data = body?.get("data") as? Map<*, *>
                        val items = data?.get("historialClinico") as? List<*>
                        historialList.clear()
                        items?.forEachIndexed { index, item ->
                            val map = item as? Map<*, *> ?: return@forEachIndexed
                            historialList.add(Historial(
                                id = index + 1,
                                consulta = map["consulta"] as? String ?: "",
                                fecha = map["fecha"] as? String ?: "",
                                diagnostico = map["diagnostico"] as? String ?: "",
                                veterinario = map["veterinario"] as? String ?: ""
                            ))
                        }
                        rvHistorialClinico.layoutManager = LinearLayoutManager(this@PetDetailActivity)
                        rvHistorialClinico.adapter = HistorialAdapter(historialList) { historial ->
                            Toast.makeText(this@PetDetailActivity, "Consulta: ${historial.consulta}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@PetDetailActivity, "Error cargando historial: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadVacunas() {
        val token = prefs.getString("token", "") ?: ""
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getVaccines(petMongoId)
                if (response.isSuccessful) {
                    val body = response.body()
                    val success = body?.get("success") as? Boolean ?: false
                    if (success) {
                        val data = body?.get("data") as? Map<*, *>
                        val items = data?.get("vacunas") as? List<*>
                        vacunasList.clear()
                        items?.forEachIndexed { index, item ->
                            val map = item as? Map<*, *> ?: return@forEachIndexed
                            vacunasList.add(Vacuna(
                                id = index + 1,
                                nombre = map["nombre"] as? String ?: "",
                                cantidad = map["cantidad"] as? String ?: "",
                                fecha = map["fecha"] as? String ?: ""
                            ))
                        }
                        rvVacunas.layoutManager = LinearLayoutManager(this@PetDetailActivity)
                        rvVacunas.adapter = VacunaAdapter(vacunasList) { vacuna ->
                            Toast.makeText(this@PetDetailActivity, "Vacuna: ${vacuna.nombre}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@PetDetailActivity, "Error cargando vacunas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            val editIntent = Intent(this, EditPetActivity::class.java)
            editIntent.putExtra("pet_id", petMongoId)
            startActivity(editIntent)
        }

        btnExportPdf.setOnClickListener {
            PdfGeneratorPet.generatePetMedicalRecord(
                context = this,
                petName = petName,
                petSpecies = petSpecies,
                petBreed = petBreed,
                petAge = petAge,
                petGender = petGender,
                petWeight = petWeight,
                petColor = petColor,
                petOwnerName = petOwnerName,
                petNotes = petNotes,
                historialList = historialList,
                vacunasList = vacunasList
            )
        }
    }
}