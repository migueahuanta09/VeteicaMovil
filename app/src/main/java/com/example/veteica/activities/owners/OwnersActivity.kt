package com.example.veteica.activities.owners

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
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.activities.payments.PaymentsActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.adapters.OwnerAdapter
import com.example.veteica.models.Owner
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class OwnersActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvOwners: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnCreateOwner: MaterialButton
    private lateinit var adapter: OwnerAdapter
    private val ownersList = mutableListOf<Owner>()
    private val originalList = mutableListOf<Owner>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owners)

        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupRecyclerView()
        setupClickListeners()
        loadOwnersWithPetCount()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvOwners = findViewById(R.id.rvOwners)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnCreateOwner = findViewById(R.id.btnCreateOwner)
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
                R.id.nav_owners -> drawerLayout.closeDrawer(GravityCompat.START)
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
        adapter = OwnerAdapter(ownersList) { owner ->
            val intent = Intent(this, OwnerDetailActivity::class.java)
            intent.putExtra("owner_mongo_id", owner.mongoId)
            intent.putExtra("owner_name", owner.name)
            intent.putExtra("owner_phone", owner.phone)
            intent.putExtra("owner_email", owner.email)
            intent.putExtra("owner_address", owner.address)
            startActivity(intent)
        }
        rvOwners.layoutManager = LinearLayoutManager(this)
        rvOwners.adapter = adapter
    }

    private fun loadOwnersWithPetCount() {
        val token = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
            .getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)

                // Llamamos ambos endpoints en paralelo
                val ownersDeferred = async { api.getOwners() }
                val petsDeferred = async { api.getPets() }

                val ownersResponse = ownersDeferred.await()
                val petsResponse = petsDeferred.await()

                if (!ownersResponse.isSuccessful) {
                    Toast.makeText(this@OwnersActivity, "Error cargando dueños", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Construir mapa de nombre -> cantidad de mascotas
                val petCountByOwner = mutableMapOf<String, Int>()
                if (petsResponse.isSuccessful) {
                    val petsData = petsResponse.body()?.get("data") as? Map<*, *>
                    val petsItems = petsData?.get("items") as? List<*>
                    petsItems?.forEach { item ->
                        val pet = item as? Map<*, *> ?: return@forEach
                        val dueno = (pet["nombreDueno"] as? String)?.trim()?.lowercase() ?: return@forEach
                        petCountByOwner[dueno] = (petCountByOwner[dueno] ?: 0) + 1
                    }
                }

                // Construir lista de dueños con conteo real
                val ownersData = ownersResponse.body()?.get("data") as? Map<*, *>
                val ownersItems = ownersData?.get("items") as? List<*>

                originalList.clear()
                ownersList.clear()

                ownersItems?.forEachIndexed { index, item ->
                    val owner = item as? Map<*, *> ?: return@forEachIndexed
                    val nombre = owner["nombre"] as? String ?: ""
                    val count = petCountByOwner[nombre.trim().lowercase()] ?: 0

                    originalList.add(Owner(
                        id = index + 1,
                        mongoId = owner["_id"] as? String ?: "",
                        uniqueCode = owner["cedula"] as? String ?: "",
                        name = nombre,
                        phone = owner["telefono"] as? String ?: "",
                        email = owner["email"] as? String ?: "",
                        address = owner["direccion"] as? String ?: "",
                        petsCount = count
                    ))
                }

                ownersList.addAll(originalList)
                adapter.updateList(ownersList)

            } catch (e: Exception) {
                Toast.makeText(this@OwnersActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnCreateOwner.setOnClickListener {
            startActivity(Intent(this, CreateOwnerActivity::class.java))
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
            ownersList.clear()
            ownersList.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.name.lowercase().contains(query) ||
                        it.uniqueCode.contains(query) ||
                        it.phone.contains(query)
            }
            ownersList.clear()
            ownersList.addAll(filtered)
        }
        adapter.updateList(ownersList)
    }

    override fun onResume() {
        super.onResume()
        loadOwnersWithPetCount()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}