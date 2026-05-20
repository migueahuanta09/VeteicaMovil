package com.example.veteica.activities.pets

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
import com.example.veteica.activities.appointments.AppointmentsActivity
import com.example.veteica.activities.auth.LoginActivity
import com.example.veteica.activities.owners.OwnersActivity
import com.example.veteica.activities.payments.PaymentsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.adapters.PetAdapter
import com.example.veteica.models.Pet

class PetsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvPets: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnCreatePet: MaterialButton
    private lateinit var adapter: PetAdapter
    private val petsList = mutableListOf<Pet>()
    private val originalList = mutableListOf<Pet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)

        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupRecyclerView()
        setupMockData()
        setupClickListeners()
    }

    private fun initViews() {
        // Verifica que todos los IDs existen en activity_pets.xml
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvPets = findViewById(R.id.rvPets)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnCreatePet = findViewById(R.id.btnCreatePet)
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
                }
                R.id.nav_owners -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, OwnersActivity::class.java))
                    finish()
                }
                R.id.nav_appointments -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    finish()
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
        adapter = PetAdapter(petsList) { pet ->
            val intent = Intent(this, PetDetailActivity::class.java)
            intent.putExtra("pet_id", pet.id)
            intent.putExtra("pet_name", pet.name)
            startActivity(intent)
        }
        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = adapter
    }

    private fun setupMockData() {
        originalList.addAll(listOf(
            Pet(1, "Max", "Perro", "Labrador", 3, 28.5, "Macho", "Dorado", "Juan Pérez", "Paciente activo"),
            Pet(2, "Luna", "Gato", "Siames", 2, 4.2, "Hembra", "Blanco", "María García", "Alérgica a pollo"),
            Pet(3, "Rocky", "Perro", "Bulldog", 5, 32.0, "Macho", "Atigrado", "Carlos López", "Requiere medicación"),
            Pet(4, "Bella", "Perro", "Poodle", 1, 6.5, "Hembra", "Blanco", "Ana Martínez", "Primera vacuna"),
            Pet(5, "Simba", "Gato", "Persa", 4, 5.8, "Macho", "Naranja", "Luis Rodríguez", "Esterilizado"),
            Pet(6, "Coco", "Perro", "Chihuahua", 2, 3.2, "Macho", "Café", "Laura Fernández", "Vacunas al día")
        ))
        petsList.addAll(originalList)
        adapter.updateList(petsList)
    }

    private fun setupClickListeners() {
        btnCreatePet.setOnClickListener {
            startActivity(Intent(this, CreatePetActivity::class.java))
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
            petsList.clear()
            petsList.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.name.lowercase().contains(query) ||
                        it.species.lowercase().contains(query) ||
                        it.ownerName.lowercase().contains(query)
            }
            petsList.clear()
            petsList.addAll(filtered)
        }
        adapter.updateList(petsList)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}