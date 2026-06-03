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
import kotlinx.coroutines.launch

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
    private lateinit var prefs: SharedPreferences

    private var petMongoId: String = ""
    private var petName: String = ""
    private var petSpecies: String = ""
    private var petBreed: String = ""
    private var petAge: Int = 0
    private var petWeight: Double = 0.0
    private var petGender: String = ""
    private var petColor: String = ""
    private var petOwnerName: String = ""
    private var petNotes: String = ""

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
        petSpecies = intent.getStringExtra("pet_species") ?: ""
        petBreed = intent.getStringExtra("pet_breed") ?: ""
        petAge = intent.getIntExtra("pet_age", 0)
        petWeight = intent.getDoubleExtra("pet_weight", 0.0)
        petGender = intent.getStringExtra("pet_gender") ?: ""
        petColor = intent.getStringExtra("pet_color") ?: ""
        petOwnerName = intent.getStringExtra("pet_owner_name") ?: ""
        petNotes = intent.getStringExtra("pet_notes") ?: ""

        tvPetName.text = petName
        tvSpecies.text = petSpecies
        tvBreed.text = petBreed
        tvAge.text = "$petAge años"
        tvGender.text = petGender
        tvOwnerName.text = petOwnerName
        tvOwnerPhone.text = ""
        tvOwnerAddress.text = ""

        if (petMongoId.isNotEmpty()) {
            loadHistorial()
            loadVacunas()
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
                    val data = body?.get("data") as? Map<*, *>
                    val items = data?.get("historialClinico") as? List<*>
                    val historialList = mutableListOf<Historial>()
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
            } catch (e: Exception) {
                Toast.makeText(this@PetDetailActivity, "Error cargando historial", Toast.LENGTH_SHORT).show()
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
                    val data = body?.get("data") as? Map<*, *>
                    val items = data?.get("vacunas") as? List<*>
                    val vacunasList = mutableListOf<Vacuna>()
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
            } catch (e: Exception) {
                Toast.makeText(this@PetDetailActivity, "Error cargando vacunas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            val editIntent = Intent(this, EditPetActivity::class.java)
            editIntent.putExtra("pet_mongo_id", petMongoId)
            editIntent.putExtra("pet_name", petName)
            editIntent.putExtra("pet_species", petSpecies)
            editIntent.putExtra("pet_breed", petBreed)
            editIntent.putExtra("pet_age", petAge)
            editIntent.putExtra("pet_weight", petWeight)
            editIntent.putExtra("pet_gender", petGender)
            editIntent.putExtra("pet_color", petColor)
            editIntent.putExtra("pet_owner_name", petOwnerName)
            editIntent.putExtra("pet_notes", petNotes)
            startActivity(editIntent)
        }
    }
}