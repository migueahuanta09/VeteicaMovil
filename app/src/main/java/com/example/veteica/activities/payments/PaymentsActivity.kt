package com.example.veteica.activities.payments

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
import com.example.veteica.activities.appointments.AppointmentsActivity
import com.example.veteica.activities.auth.LoginActivity
import com.example.veteica.activities.owners.OwnersActivity
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.activities.pets.PetsActivity
import com.example.veteica.activities.profile.ProfileActivity
import com.example.veteica.adapters.ProductAdapter
import com.example.veteica.adapters.ServiceAdapter
import com.example.veteica.adapters.PendingPaymentAdapter
import com.example.veteica.models.Product
import com.example.veteica.models.Service
import com.example.veteica.models.PendingPayment
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class PaymentsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvServices: RecyclerView
    private lateinit var rvPendingPayments: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnCreateProduct: MaterialButton
    private lateinit var btnCreateService: MaterialButton
    private lateinit var productAdapter: ProductAdapter
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var pendingAdapter: PendingPaymentAdapter
    private lateinit var prefs: SharedPreferences
    private val productsList = mutableListOf<Product>()
    private val originalProductsList = mutableListOf<Product>()
    private val servicesList = mutableListOf<Service>()
    private val originalServicesList = mutableListOf<Service>()
    private val pendingList = mutableListOf<PendingPayment>()
    private val originalPendingList = mutableListOf<PendingPayment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        setupDrawerLayout()
        setupRecyclerViews()
        loadData()
        setupClickListeners()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)
        rvProducts = findViewById(R.id.rvProducts)
        rvServices = findViewById(R.id.rvServices)
        rvPendingPayments = findViewById(R.id.rvPendingPayments)
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
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, HomeActivity::class.java)); finish() }
                R.id.nav_pets -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, PetsActivity::class.java)); finish() }
                R.id.nav_owners -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, OwnersActivity::class.java)); finish() }
                R.id.nav_appointments -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, AppointmentsActivity::class.java)); finish() }
                R.id.nav_payments -> { drawerLayout.closeDrawer(GravityCompat.START) }
                R.id.nav_profile -> { drawerLayout.closeDrawer(GravityCompat.START); startActivity(Intent(this, ProfileActivity::class.java)); finish() }
                R.id.nav_logout -> { prefs.edit().remove("token").apply(); startActivity(Intent(this, LoginActivity::class.java)); finishAffinity() }
            }
            true
        }

        val headerView = navView.getHeaderView(0)
        val tvNavUserName: TextView = headerView.findViewById(R.id.tvNavUserName)
        tvNavUserName.text = "Dra. María González"
    }

    private fun setupRecyclerViews() {
        pendingAdapter = PendingPaymentAdapter(pendingList) { payment ->
            if (payment.status == "Pendiente") {
                val intent = Intent(this, PendingPaymentDetailActivity::class.java)
                intent.putExtra("payment_id", payment.id)
                intent.putExtra("payment_mongo_id", payment.mongoId)
                intent.putExtra("payment_pet", payment.petName)
                intent.putExtra("payment_service", payment.serviceName)
                intent.putExtra("payment_date", payment.date)
                intent.putExtra("payment_total", payment.total)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Este cobro ya fue realizado", Toast.LENGTH_SHORT).show()
            }
        }
        rvPendingPayments.layoutManager = LinearLayoutManager(this)
        rvPendingPayments.adapter = pendingAdapter

        productAdapter = ProductAdapter(productsList) { product ->
            val intent = Intent(this, PaymentDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            intent.putExtra("product_mongo_id", product.mongoId)
            intent.putExtra("product_name", product.name)
            startActivity(intent)
        }
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = productAdapter

        serviceAdapter = ServiceAdapter(servicesList) { service ->
            val intent = Intent(this, ServiceDetailActivity::class.java)
            intent.putExtra("service_id", service.id)
            intent.putExtra("service_mongo_id", service.mongoId)
            intent.putExtra("service_name", service.name)
            startActivity(intent)
        }
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = serviceAdapter
    }

    private fun loadData() {
        val token = prefs.getString("token", "") ?: ""
        if (token.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            loadPendingPayments(token)
            loadProducts(token)
            loadServices(token)
        }
    }

    private suspend fun loadPendingPayments(token: String) {
        try {
            val api = RetrofitClient.instanceWithToken(token)
            val response = api.getPendingPayments()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.get("data") as? Map<*, *>
                val items = data?.get("items") as? List<*>
                originalPendingList.clear()
                items?.forEachIndexed { index, item ->
                    val map = item as? Map<*, *> ?: return@forEachIndexed
                    originalPendingList.add(PendingPayment(
                        id = index + 1,
                        mongoId = map["_id"] as? String ?: "",
                        petName = map["nombreMascota"] as? String ?: "",
                        serviceName = map["nombreServicio"] as? String ?: "Consulta general",
                        date = map["fecha"] as? String ?: "",
                        total = (map["total"] as? Double) ?: 0.0,
                        status = map["estado"] as? String ?: "Pendiente"
                    ))
                }
                pendingList.clear()
                pendingList.addAll(originalPendingList)
                pendingAdapter.updateList(pendingList)
            }
        } catch (e: Exception) {
            Toast.makeText(this@PaymentsActivity, "Error cargando pagos", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadProducts(token: String) {
        try {
            val api = RetrofitClient.instanceWithToken(token)
            val response = api.getProducts()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.get("data") as? Map<*, *>
                val items = data?.get("items") as? List<*>
                originalProductsList.clear()
                items?.forEachIndexed { index, item ->
                    val map = item as? Map<*, *> ?: return@forEachIndexed
                    originalProductsList.add(Product(
                        id = index + 1,
                        mongoId = map["_id"] as? String ?: "",
                        name = map["nombre"] as? String ?: "",
                        stock = ((map["existencia"] as? Double)?.toInt()) ?: 0,
                        price = (map["precio"] as? Double) ?: 0.0,
                        expiryDate = map["fechaCaducidad"] as? String ?: "",
                        dose = map["dosis"] as? String ?: "",
                        indications = map["indicaciones"] as? String ?: "",
                        formula = map["formula"] as? String ?: "",
                        administration = map["administracion"] as? String ?: ""
                    ))
                }
                productsList.clear()
                productsList.addAll(originalProductsList)
                productAdapter.updateList(productsList)
            }
        } catch (e: Exception) {
            Toast.makeText(this@PaymentsActivity, "Error cargando productos", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadServices(token: String) {
        try {
            val api = RetrofitClient.instanceWithToken(token)
            val response = api.getServices()
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.get("data") as? Map<*, *>
                val items = data?.get("items") as? List<*>
                originalServicesList.clear()
                items?.forEachIndexed { index, item ->
                    val map = item as? Map<*, *> ?: return@forEachIndexed
                    originalServicesList.add(Service(
                        id = index + 1,
                        mongoId = map["_id"] as? String ?: "",
                        name = map["nombre"] as? String ?: "",
                        quantity = ((map["cantidad"] as? Double)?.toInt()) ?: 0,
                        vets = ((map["veterinarios"] as? Double)?.toInt()) ?: 0,
                        price = (map["precio"] as? Double) ?: 0.0
                    ))
                }
                servicesList.clear()
                servicesList.addAll(originalServicesList)
                serviceAdapter.updateList(servicesList)
            }
        } catch (e: Exception) {
            Toast.makeText(this@PaymentsActivity, "Error cargando servicios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        btnCreateProduct.setOnClickListener { startActivity(Intent(this, CreatePaymentActivity::class.java)) }
        btnCreateService.setOnClickListener { startActivity(Intent(this, CreateServiceActivity::class.java)) }
        btnSearch.setOnClickListener { performSearch() }
        etSearch.setOnEditorActionListener { _, _, _ -> performSearch(); true }
    }

    private fun performSearch() {
        val query = etSearch.text.toString().lowercase().trim()
        if (query.isEmpty()) {
            pendingList.clear(); pendingList.addAll(originalPendingList)
            productsList.clear(); productsList.addAll(originalProductsList)
            servicesList.clear(); servicesList.addAll(originalServicesList)
        } else {
            pendingList.clear()
            pendingList.addAll(originalPendingList.filter { it.petName.lowercase().contains(query) })
            productsList.clear()
            productsList.addAll(originalProductsList.filter { it.name.lowercase().contains(query) })
            servicesList.clear()
            servicesList.addAll(originalServicesList.filter { it.name.lowercase().contains(query) })
        }
        pendingAdapter.updateList(pendingList)
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