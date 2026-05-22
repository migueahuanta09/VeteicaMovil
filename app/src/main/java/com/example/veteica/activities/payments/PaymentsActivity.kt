package com.example.veteica.activities.payments

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
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.adapters.ProductAdapter
import com.example.veteica.adapters.ServiceAdapter
import com.example.veteica.models.Product
import com.example.veteica.models.Service

class PaymentsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvServices: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnCreateProduct: MaterialButton
    private lateinit var btnCreateService: MaterialButton
    private lateinit var productAdapter: ProductAdapter
    private lateinit var serviceAdapter: ServiceAdapter
    private val productsList = mutableListOf<Product>()
    private val originalProductsList = mutableListOf<Product>()
    private val servicesList = mutableListOf<Service>()
    private val originalServicesList = mutableListOf<Service>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)

        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupRecyclerViews()
        setupMockData()
        setupClickListeners()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvProducts = findViewById(R.id.rvProducts)
        rvServices = findViewById(R.id.rvServices)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnCreateProduct = findViewById(R.id.btnCreateProduct)
        btnCreateService = findViewById(R.id.btnCreateService)
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
                    startActivity(Intent(this, AppointmentsActivity::class.java))
                    finish()
                }
                R.id.nav_payments -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
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
                else -> true
            }
            true
        }

        val headerView = navView.getHeaderView(0)
        val tvNavUserName: TextView = headerView.findViewById(R.id.tvNavUserName)
        tvNavUserName.text = "Dra. María González"
    }

    private fun setupRecyclerViews() {
        // Adaptador para productos
        productAdapter = ProductAdapter(productsList) { product ->
            val intent = Intent(this, PaymentDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            intent.putExtra("product_name", product.name)
            startActivity(intent)
        }
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = productAdapter

        // Adaptador para servicios
        serviceAdapter = ServiceAdapter(servicesList) { service ->
            val intent = Intent(this, ServiceDetailActivity::class.java)
            intent.putExtra("service_id", service.id)
            intent.putExtra("service_name", service.name)
            startActivity(intent)
        }
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = serviceAdapter
    }

    private fun setupMockData() {
        // Datos de prueba para productos
        originalProductsList.addAll(listOf(
            Product(1, "Nobivac", 10, 1000.0, "20/10/2030", "1 dosis",
                "Para la inmunización activa contra la rabia de perros, gatos y turones.",
                "Cultivo del virus de la rabia, clonado de la cepa Pasteur RIVM",
                "Por vía subcutánea o intramuscular."),
            Product(2, "Bravecto", 15, 1200.0, "15/12/2028", "1 tableta",
                "Tratamiento de infestaciones por pulgas y garrapatas",
                "Fluralaner", "Oral"),
            Product(3, "Leptospirosis", 20, 2000.0, "10/05/2029", "1 dosis",
                "Inmunización contra leptospirosis canina",
                "Bacterina inactivada", "Subcutánea")
        ))
        productsList.addAll(originalProductsList)
        productAdapter.updateList(productsList)

        // Datos de prueba para servicios
        originalServicesList.addAll(listOf(
            Service(1, "Citas", 15, 2, 100.0),
            Service(2, "Cirugías", 6, 1, 21200.0),
            Service(3, "Observación", 10, 1, 600.0),
            Service(4, "Barro relajante", 7, 1, 2000.0)
        ))
        servicesList.addAll(originalServicesList)
        serviceAdapter.updateList(servicesList)
    }

    private fun setupClickListeners() {
        btnCreateProduct.setOnClickListener {
            startActivity(Intent(this, CreatePaymentActivity::class.java))
        }

        btnCreateService.setOnClickListener {
            startActivity(Intent(this, CreateServiceActivity::class.java))
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
            productsList.clear()
            productsList.addAll(originalProductsList)
            servicesList.clear()
            servicesList.addAll(originalServicesList)
        } else {
            val filteredProducts = originalProductsList.filter {
                it.id.toString().contains(query) ||
                        it.name.lowercase().contains(query)
            }
            productsList.clear()
            productsList.addAll(filteredProducts)

            val filteredServices = originalServicesList.filter {
                it.id.toString().contains(query) ||
                        it.name.lowercase().contains(query)
            }
            servicesList.clear()
            servicesList.addAll(filteredServices)
        }

        productAdapter.updateList(productsList)
        serviceAdapter.updateList(servicesList)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}