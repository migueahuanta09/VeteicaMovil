package com.example.veteica.activities.appointments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import com.example.veteica.activities.auth.LoginActivity
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.activities.payments.PaymentsActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.activities.owners.OwnersActivity
import com.example.veteica.adapters.AppointmentAdapter
import com.example.veteica.models.Appointment
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvAppointments: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnCreateAppointment: MaterialButton
    private lateinit var adapter: AppointmentAdapter
    private lateinit var prefs: SharedPreferences
    private val appointmentsList = mutableListOf<Appointment>()
    private val originalList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)

        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupRecyclerView()
        loadAppointments()
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
                R.id.nav_home -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, HomeActivity::class.java)); finish() }
                R.id.nav_pets -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, PetsActivity::class.java)); finish() }
                R.id.nav_owners -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, OwnersActivity::class.java)); finish() }
                R.id.nav_appointments -> { drawerLayout.closeDrawer(GravityCompat.START) }
                R.id.nav_payments -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, PaymentsActivity::class.java)); finish() }
                R.id.nav_profile -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, ProfileActivity::class.java)); finish() }
                R.id.nav_logout -> { prefs.edit().remove("token").apply(); startActivity(Intent(this, LoginActivity::class.java)); finishAffinity() }
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

    private fun loadAppointments() {
        val token = prefs.getString("token", "") ?: ""
        if (token.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getAppointments()

                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    val items = data?.get("items") as? List<*>

                    originalList.clear()
                    items?.forEach { item ->
                        val map = item as? Map<*, *> ?: return@forEach
                        originalList.add(Appointment(
                            id = (map["_id"] as? String)?.hashCode() ?: 0,
                            date = map["fecha"] as? String ?: "",
                            time = map["hora"] as? String ?: "",
                            petName = map["nombreMascota"] as? String ?: "",
                            ownerName = map["nombreDueno"] as? String ?: "",
                            veterinarian = map["veterinario"] as? String ?: "",
                            reason = map["motivo"] as? String ?: "",
                            status = map["estado"] as? String ?: "Pendiente"
                        ))
                    }
                    appointmentsList.clear()
                    appointmentsList.addAll(originalList)
                    adapter.updateList(appointmentsList)
                } else {
                    Toast.makeText(this@AppointmentsActivity, "Error al cargar citas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AppointmentsActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnCreateAppointment.setOnClickListener {
            startActivity(Intent(this, CreateAppointmentActivity::class.java))
        }

        btnSearch.setOnClickListener { performSearch() }

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