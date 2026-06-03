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
import androidx.lifecycle.lifecycleScope
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
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

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
        setupClickListeners()
        loadPets()
    }

    private fun initViews() {
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
                R.id.nav_pets -> drawerLayout.closeDrawer(GravityCompat.START)
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
            intent.putExtra("pet_mongo_id", pet.mongoId)
            startActivity(intent)
        }
        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = adapter
    }

    private fun loadPets() {
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

                    originalList.clear()
                    petsList.clear()

                    items?.forEachIndexed { index, item ->
                        val pet = item as? Map<*, *> ?: return@forEachIndexed
                        val mongoId = pet["_id"] as? String ?: ""
                        val nombre = pet["nombre"] as? String ?: ""
                        val especie = pet["especie"] as? String ?: ""
                        val raza = pet["raza"] as? String ?: ""
                        val edad = (pet["edad"] as? Double)?.toInt() ?: 0
                        val peso = pet["peso"] as? Double ?: 0.0
                        val genero = pet["genero"] as? String ?: ""
                        val color = pet["color"] as? String ?: ""
                        val dueno = pet["nombreDueno"] as? String ?: ""
                        val notas = pet["notas"] as? String ?: ""

                        originalList.add(Pet(
                            id = index + 1,
                            mongoId = mongoId,
                            name = nombre,
                            species = especie,
                            breed = raza,
                            age = edad,
                            weight = peso,
                            gender = genero,
                            color = color,
                            ownerName = dueno,
                            notes = notas
                        ))
                    }

                    petsList.addAll(originalList)
                    adapter.updateList(petsList)

                } else {
                    Toast.makeText(this@PetsActivity, "Error cargando mascotas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PetsActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnCreatePet.setOnClickListener {
            startActivity(Intent(this, CreatePetActivity::class.java))
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

    override fun onResume() {
        super.onResume()
        loadPets()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}