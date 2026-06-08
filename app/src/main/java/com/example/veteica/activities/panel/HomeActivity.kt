package com.example.veteica.activities.panel

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.example.veteica.R
import com.example.veteica.activities.appointments.AppointmentsActivity
import com.example.veteica.activities.appointments.CreateAppointmentActivity
import com.example.veteica.activities.auth.LoginActivity
import com.example.veteica.activities.owners.CreateOwnerActivity
import com.example.veteica.activities.owners.OwnersActivity
import com.example.veteica.activities.payments.CreatePaymentActivity
import com.example.veteica.activities.payments.PaymentsActivity
import com.example.veteica.activities.pets.CreatePetActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.adapters.AppointmentAdapter
import com.example.veteica.models.Appointment
import com.example.veteica.network.RetrofitClient
import com.example.veteica.views.PieChartView
import com.example.veteica.views.BarChartView
import com.example.veteica.views.LineChartView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvNextAppointments: RecyclerView
    private lateinit var prefs: SharedPreferences
    private lateinit var tvUserName: TextView
    private lateinit var tvTotalPets: TextView
    private lateinit var tvTodayAppointments: TextView
    private lateinit var tvDiseasesLegend: TextView
    private lateinit var pieChartDiseases: PieChartView
    private lateinit var barChartSpecies: BarChartView
    private lateinit var lineChartAge: LineChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupClickListeners()
        loadDashboardData()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvNextAppointments = findViewById(R.id.rvNextAppointments)
        tvUserName = findViewById(R.id.tvUserName)
        tvTotalPets = findViewById(R.id.tvTotalPets)
        tvTodayAppointments = findViewById(R.id.tvTodayAppointments)
        tvDiseasesLegend = findViewById(R.id.tvDiseasesLegend)
        pieChartDiseases = findViewById(R.id.pieChartDiseases)
        barChartSpecies = findViewById(R.id.barChartSpecies)
        lineChartAge = findViewById(R.id.lineChartAge)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupDrawerLayout() {
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.nav_pets -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, PetsActivity::class.java))
                }
                R.id.nav_owners -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, OwnersActivity::class.java))
                }
                R.id.nav_appointments -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                }
                R.id.nav_payments -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, PaymentsActivity::class.java))
                }
                R.id.nav_profile -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    prefs.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
            }
            true
        }

        val headerView = navView.getHeaderView(0)
        val tvNavUserName: TextView = headerView.findViewById(R.id.tvNavUserName)
        tvNavUserName.text = prefs.getString("user_name", "Veterinario") ?: "Veterinario"
    }

    private fun loadDashboardData() {
        val token = prefs.getString("token", "") ?: ""
        if (token.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        tvUserName.text = prefs.getString("user_name", "Veterinario") ?: "Veterinario"

        lifecycleScope.launch {
            val petsDeferred = async { loadPetsData(token) }
            val appointmentsDeferred = async { loadAppointments(token) }
            petsDeferred.await()
            appointmentsDeferred.await()
        }
    }

    private suspend fun loadPetsData(token: String) {
        try {
            val api = RetrofitClient.instanceWithToken(token)
            val response = api.getPets()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.get("data") as? Map<*, *>
                val items = data?.get("items") as? List<*>
                val total = items?.size ?: 0
                tvTotalPets.text = total.toString()

                val speciesCount = mutableMapOf<String, Int>()
                val diseasesCount = mutableMapOf<String, Int>()
                val ageCount = mutableMapOf<String, Int>()

                items?.forEach { item ->
                    val pet = item as? Map<*, *> ?: return@forEach
                    val especie = (pet["especie"] as? String) ?: "Otro"
                    val enfermedad = obtenerEnfermedadAleatoria()
                    val edad = (pet["edad"] as? Number)?.toInt() ?: 0

                    speciesCount[especie] = (speciesCount[especie] ?: 0) + 1
                    diseasesCount[enfermedad] = (diseasesCount[enfermedad] ?: 0) + 1

                    when {
                        edad < 1 -> ageCount["0-1 año"] = (ageCount["0-1 año"] ?: 0) + 1
                        edad in 1..3 -> ageCount["1-3 años"] = (ageCount["1-3 años"] ?: 0) + 1
                        edad in 3..5 -> ageCount["3-5 años"] = (ageCount["3-5 años"] ?: 0) + 1
                        edad in 5..7 -> ageCount["5-7 años"] = (ageCount["5-7 años"] ?: 0) + 1
                        edad in 7..9 -> ageCount["7-9 años"] = (ageCount["7-9 años"] ?: 0) + 1
                        else -> ageCount["9+ años"] = (ageCount["9+ años"] ?: 0) + 1
                    }
                }

                // Gráfica 1: Enfermedades más comunes (PIE)
                val diseasesColors = listOf(
                    "#F44336", "#FF9800", "#2196F3", "#4CAF50", "#9C27B0", "#00BCD4"
                )
                val diseasesPieData = diseasesCount.entries.mapIndexed { index, entry ->
                    val pct = entry.value.toFloat() / total * 100
                    PieChartView.PieData(
                        "${entry.key} (${entry.value})",
                        pct,
                        Color.parseColor(diseasesColors[index % diseasesColors.size])
                    )
                }
                if (diseasesPieData.isNotEmpty()) {
                    pieChartDiseases.setData(diseasesPieData)
                }
                tvDiseasesLegend.text = diseasesCount.entries.joinToString("\n") { "${it.key}: ${it.value}" }

                // Gráfica 2: Tipos de mascotas (BARRAS)
                val speciesData = speciesCount.entries.map { entry ->
                    val pct = entry.value.toFloat() / total * 100
                    Pair(entry.key, pct)
                }.sortedByDescending { it.second }
                barChartSpecies.setData(speciesData)

                // Gráfica 3: Distribución por Edad (LINEAL)
                val ageRanges = listOf("0-1 año", "1-3 años", "3-5 años", "5-7 años", "7-9 años", "9+ años")
                val agePercentages = ageRanges.map { range ->
                    val count = ageCount[range] ?: 0
                    if (total > 0) (count.toFloat() / total * 100) else 0f
                }
                lineChartAge.setData(agePercentages, ageRanges)
                lineChartAge.setLineColor(Color.parseColor("#2E7D32"))
            }
        } catch (e: Exception) {
            tvTotalPets.text = "0"
        }
    }

    private fun obtenerEnfermedadAleatoria(): String {
        val enfermedades = listOf(
            "Infección respiratoria",
            "Problemas digestivos",
            "Dermatitis",
            "Otitis",
            "Parásitos internos",
            "Problemas dentales"
        )
        return enfermedades.random()
    }

    private suspend fun loadAppointments(token: String) {
        try {
            val api = RetrofitClient.instanceWithToken(token)
            val response = api.getAppointments()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.get("data") as? Map<*, *>
                val items = data?.get("items") as? List<*>

                val appointments = mutableListOf<Appointment>()
                items?.forEach { item ->
                    val map = item as? Map<*, *> ?: return@forEach
                    val status = map["estado"] as? String ?: "Pendiente"
                    if (status == "Pendiente" || status == "Confirmada") {
                        appointments.add(Appointment(
                            id = (map["_id"] as? String)?.hashCode() ?: 0,
                            petName = map["nombreMascota"] as? String ?: "",
                            date = map["fecha"] as? String ?: "",
                            time = map["hora"] as? String ?: "",
                            reason = map["motivo"] as? String ?: "",
                            status = status
                        ))
                    }
                }

                val pendingCount = appointments.count()
                tvTodayAppointments.text = pendingCount.toString()

                val nextAppointments = appointments.take(3)
                rvNextAppointments.layoutManager = LinearLayoutManager(this@HomeActivity)
                rvNextAppointments.adapter = AppointmentAdapter(nextAppointments) { appointment ->
                    Toast.makeText(this@HomeActivity, "Cita: ${appointment.petName}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            tvTodayAppointments.text = "0"
        }
    }

    private fun setupClickListeners() {
        findViewById<androidx.cardview.widget.CardView>(R.id.cardPets).setOnClickListener {
            startActivity(Intent(this, PetsActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.cardAppointments).setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }
        findViewById<TextView>(R.id.tvViewAllAppointments).setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnNewPet).setOnClickListener {
            startActivity(Intent(this, CreatePetActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnNewAppointment).setOnClickListener {
            startActivity(Intent(this, CreateAppointmentActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnNewPayment).setOnClickListener {
            startActivity(Intent(this, CreatePaymentActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnOwner).setOnClickListener {
            startActivity(Intent(this, CreateOwnerActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}