package com.example.veteica.activities.appointments

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import com.example.veteica.activities.auth.LoginActivity
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.activities.payments.PaymentsActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.activities.owners.OwnersActivity
import com.example.veteica.adapters.AppointmentAdapter
import com.example.veteica.models.Appointment

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvAppointments: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnCreateAppointment: MaterialButton
    private lateinit var adapter: AppointmentAdapter
    private val appointmentsList = mutableListOf<Appointment>()
    private val originalList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupRecyclerView()
        setupMockData()
        setupClickListeners()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvAppointments = findViewById(R.id.rvAppointments)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnCreateAppointment = findViewById(R.id.btnCreateAppointment)
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
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                R.id.nav_pets -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, PetsActivity::class.java))
                    finish()
                }
                R.id.nav_owners -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, OwnersActivity::class.java))
                    finish()
                }
                R.id.nav_appointments -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_payments -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, PaymentsActivity::class.java))
                    finish()
                }
                R.id.nav_profile -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
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

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(appointmentsList) { appointment ->
            val intent = Intent(this, AppointmentDetailActivity::class.java)
            intent.putExtra("appointment_id", appointment.id)
            intent.putExtra("appointment_pet", appointment.petName)
            startActivity(intent)
        }
        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = adapter
    }

    private fun setupMockData() {
        originalList.addAll(listOf(
            Appointment(1, "01/06/2026", "10:00 AM", "Lilo", "Jose Herrera", "Navarro admin", "Consulta general", "Pendiente"),
            Appointment(2, "26/11/2025", "10:00 AM", "Lilo", "Jose Herrera", "Navarro admin", "Revisión", "Confirmada"),
            Appointment(3, "25/11/2025", "10:00 AM", "Lilo", "Jose Herrera", "Navarro admin", "Vacunación", "Completada"),
            Appointment(4, "20/11/2025", "03:00 PM", "Max", "Juan Pérez", "Dra. María", "Consulta general", "Pendiente"),
            Appointment(5, "15/11/2025", "11:30 AM", "Luna", "María García", "Dr. Carlos", "Revisión", "Confirmada")
        ))
        appointmentsList.addAll(originalList)
        adapter.updateList(appointmentsList)
    }

    private fun setupClickListeners() {
        btnCreateAppointment.setOnClickListener {
            startActivity(Intent(this, CreateAppointmentActivity::class.java))
        }

        btnSearch.setOnClickListener {
            performSearch()
        }

        etSearch.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun performSearch() {
        val query = etSearch.text.toString().lowercase().trim()
        if (query.isEmpty()) {
            appointmentsList.clear()
            appointmentsList.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.id.toString().contains(query) ||
                        it.petName.lowercase().contains(query) ||
                        it.ownerName.lowercase().contains(query)
            }
            appointmentsList.clear()
            appointmentsList.addAll(filtered)
        }
        adapter.updateList(appointmentsList)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}