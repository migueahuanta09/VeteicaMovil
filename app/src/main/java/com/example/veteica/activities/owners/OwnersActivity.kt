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
            intent.putExtra("owner_name", owner.name)
            startActivity(intent)
        }
        rvOwners.layoutManager = LinearLayoutManager(this)
        rvOwners.adapter = adapter
    }

    private fun setupMockData() {
        originalList.addAll(listOf(
            Owner(1, "Juan Pérez", "555-1234-567", "juan@email.com", "Av. Principal #123", 2),
            Owner(2, "María García", "555-2345-678", "maria@email.com", "Calle Reforma #456", 1),
            Owner(3, "Carlos López", "555-3456-789", "carlos@email.com", "Blvd. Centro #789", 3),
            Owner(4, "Ana Martínez", "555-4567-890", "ana@email.com", "Privada del Parque #12", 1),
            Owner(5, "Luis Rodríguez", "555-5678-901", "luis@email.com", "Av. Universidad #234", 2),
            Owner(6, "Laura Fernández", "555-6789-012", "laura@email.com", "Calle Jardín #567", 1)
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
                it.name.lowercase().contains(query) ||
                        it.phone.contains(query) ||
                        it.email.lowercase().contains(query)
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