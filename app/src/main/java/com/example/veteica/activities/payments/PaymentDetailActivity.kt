package com.example.veteica.activities.payments

import android.content.Intent  // 👈 AGREGA ESTA LÍNEA
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
        val productName = intent.getStringExtra("product_name") ?: "Nobivac"

        tvProductName.text = productName
        tvExpiryDate.text = "20/10/2030"
        tvDosage.text = "1 dosis"
        tvPrice.text = "$1,000"
        tvIndications.text = "Para la inmunización activa contra la rabia de perros, gatos y turones."
        tvFormula.text = "Cultivo del virus de la rabia, clonado de la cepa Pasteur RIVM"
        tvAdministration.text = "Por vía subcutánea o intramuscular."
        tvStock.text = "10 unidades"
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditPaymentActivity::class.java)
            intent.putExtra("product_id", productId)
            intent.putExtra("product_name", tvProductName.text.toString())
            startActivity(intent)
        }

        btnSelect.setOnClickListener {
            Toast.makeText(this, "Producto seleccionado para cobro", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}