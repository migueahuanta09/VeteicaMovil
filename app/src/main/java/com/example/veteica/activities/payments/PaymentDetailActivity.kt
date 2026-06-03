package com.example.veteica.activities.payments

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class PaymentDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: TextView
    private lateinit var btnSelect: Button
    private lateinit var btnCancel: Button
    private lateinit var tvProductName: TextView
    private lateinit var tvExpiryDate: TextView
    private lateinit var tvDosage: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvIndications: TextView
    private lateinit var tvFormula: TextView
    private lateinit var tvAdministration: TextView
    private lateinit var tvStock: TextView

    private var productId: Int = 0
    private var productMongoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_detail)

        initViews()
        setupToolbar()
        loadProductData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        btnSelect = findViewById(R.id.btnSelect)
        btnCancel = findViewById(R.id.btnCancel)
        tvProductName = findViewById(R.id.tvProductName)
        tvExpiryDate = findViewById(R.id.tvExpiryDate)
        tvDosage = findViewById(R.id.tvDosage)
        tvPrice = findViewById(R.id.tvPrice)
        tvIndications = findViewById(R.id.tvIndications)
        tvFormula = findViewById(R.id.tvFormula)
        tvAdministration = findViewById(R.id.tvAdministration)
        tvStock = findViewById(R.id.tvStock)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val productName = intent.getStringExtra("product_name") ?: "Producto"
        val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        toolbarTitle.text = "Cobro de $productName"
    }

    private fun loadProductData() {
        productId = intent.getIntExtra("product_id", 1)
        productMongoId = intent.getStringExtra("product_mongo_id") ?: ""

        tvProductName.text = intent.getStringExtra("product_name") ?: ""
        tvExpiryDate.text = intent.getStringExtra("product_expiry") ?: ""
        tvDosage.text = intent.getStringExtra("product_dose") ?: ""
        tvPrice.text = "$${intent.getDoubleExtra("product_price", 0.0)}"
        tvIndications.text = intent.getStringExtra("product_indications") ?: ""
        tvFormula.text = intent.getStringExtra("product_formula") ?: ""
        tvAdministration.text = intent.getStringExtra("product_administration") ?: ""
        tvStock.text = "${intent.getIntExtra("product_stock", 0)} unidades"
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditPaymentActivity::class.java)
            intent.putExtra("product_id", productId)
            intent.putExtra("product_mongo_id", productMongoId)
            intent.putExtra("product_name", tvProductName.text.toString())
            startActivity(intent)
        }

        btnSelect.setOnClickListener {
            Toast.makeText(this, "Producto seleccionado para cobro", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener { finish() }
    }
}