package com.example.veteica.activities.panel

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.example.veteica.R
import com.example.veteica.activities.appointments.AppointmentsActivity
import com.example.veteica.activities.appointments.CreateAppointmentActivity
import com.example.veteica.activities.auth.LoginActivity
import com.example.veteica.activities.owners.OwnersActivity
import com.example.veteica.activities.payments.CreatePaymentActivity
import com.example.veteica.activities.payments.PaymentsActivity
import com.example.veteica.activities.pets.CreatePetActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.adapters.AppointmentAdapter
import com.example.veteica.models.Appointment
import com.example.veteica.views.PieChartView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvNextAppointments: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupMockData()
        setupClickListeners()
        setupPieCharts()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvNextAppointments = findViewById(R.id.rvNextAppointments)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupDrawerLayout() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
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
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
            }
            true
        }

        val headerView = navView.getHeaderView(0)
        val tvNavUserName: TextView = headerView.findViewById(R.id.tvNavUserName)
        tvNavUserName.text = "Dra. María González"
    }

    private fun setupMockData() {
        val tvUserName: TextView = findViewById(R.id.tvUserName)
        val tvTotalPets: TextView = findViewById(R.id.tvTotalPets)
        val tvTodayAppointments: TextView = findViewById(R.id.tvTodayAppointments)

        tvUserName.text = "Dra. María González"
        tvTotalPets.text = "48"
        tvTodayAppointments.text = "12"

        val mockAppointments = listOf(
            Appointment(1, "Max", "15/01/2025", "10:00", "Consulta general", "Pendiente"),
            Appointment(2, "Luna", "15/01/2025", "11:30", "Vacunación", "Confirmada"),
            Appointment(3, "Rocky", "15/01/2025", "14:00", "Cirugía", "Pendiente")
        )

        rvNextAppointments.layoutManager = LinearLayoutManager(this)
        rvNextAppointments.adapter = AppointmentAdapter(mockAppointments) { appointment ->
            Toast.makeText(this, "Cita: ${appointment.petName}", Toast.LENGTH_SHORT).show()
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

        findViewById<MaterialButton>(R.id.btnReports).setOnClickListener {
            Toast.makeText(this, "Reportes - Próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPieCharts() {
        // Gráfica de enfermedades
        val pieChartDiseases = findViewById<PieChartView>(R.id.pieChartDiseases)
        val diseasesData = listOf(
            PieChartView.PieData("Infecciones", 45f, android.graphics.Color.parseColor("#4CAF50")),
            PieChartView.PieData("Respiratorias", 30f, android.graphics.Color.parseColor("#FF9800")),
            PieChartView.PieData("Digestivas", 25f, android.graphics.Color.parseColor("#F44336"))
        )
        pieChartDiseases.setData(diseasesData)

        // Gráfica de tipos de mascotas
        val pieChartPets = findViewById<PieChartView>(R.id.pieChartPets)
        val petsData = listOf(
            PieChartView.PieData("Perros", 60f, android.graphics.Color.parseColor("#4CAF50")),
            PieChartView.PieData("Gatos", 25f, android.graphics.Color.parseColor("#FF9800")),
            PieChartView.PieData("Otros", 15f, android.graphics.Color.parseColor("#2196F3"))
        )
        pieChartPets.setData(petsData)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}