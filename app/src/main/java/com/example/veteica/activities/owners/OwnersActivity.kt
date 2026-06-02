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
        setupMockData()
        setupClickListeners()
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
                R.id.nav_owners -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
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
        adapter = OwnerAdapter(ownersList) { owner ->
            val intent = Intent(this, OwnerDetailActivity::class.java)
            intent.putExtra("owner_id", owner.id)
            intent.putExtra("owner_code", owner.uniqueCode)  // 👈 CORREGIDO
            intent.putExtra("owner_name", owner.name)
            startActivity(intent)
        }
        rvOwners.layoutManager = LinearLayoutManager(this)
        rvOwners.adapter = adapter
    }

    private fun setupMockData() {
        originalList.addAll(listOf(
            Owner(1, "12345678", "José Herrera", "612-123-4567", "jose@gmail.com", "Colonia Púrpura #123", 2, null),
            Owner(2, "87654321", "María García", "612-234-5678", "maria@gmail.com", "Calle Reforma #456", 1, null),
            Owner(3, "11223344", "Carlos López", "612-345-6789", "carlos@gmail.com", "Blvd. Centro #789", 3, null),
            Owner(4, "44332211", "Ana Martínez", "612-456-7890", "ana@gmail.com", "Privada del Parque #12", 1, null),
            Owner(5, "55667788", "Luis Rodríguez", "612-567-8901", "luis@gmail.com", "Av. Universidad #234", 2, null)
        ))
        ownersList.addAll(originalList)
        adapter.updateList(ownersList)
    }

    private fun setupClickListeners() {
        btnCreateOwner.setOnClickListener {
            startActivity(Intent(this, CreateOwnerActivity::class.java))
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
            ownersList.clear()
            ownersList.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.id.toString().contains(query) ||
                        it.name.lowercase().contains(query) ||
                        it.uniqueCode.contains(query) ||
                        it.phone.contains(query)
            }
            ownersList.clear()
            ownersList.addAll(filtered)
        }
        adapter.updateList(ownersList)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}