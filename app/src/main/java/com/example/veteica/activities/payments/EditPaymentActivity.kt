package com.example.veteica.activities.payments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class EditPaymentActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etProductName: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etDosage: EditText
    private lateinit var etPrice: EditText
    private lateinit var etIndications: EditText
    private lateinit var etFormula: EditText
    private lateinit var etAdministration: EditText
    private lateinit var etStock: EditText
    private lateinit var prefs: SharedPreferences

    private var productMongoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_payment)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        loadProductData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        etProductName = findViewById(R.id.etProductName)
        etExpiryDate = findViewById(R.id.etExpiryDate)
        etDosage = findViewById(R.id.etDosage)
        etPrice = findViewById(R.id.etPrice)
        etIndications = findViewById(R.id.etIndications)
        etFormula = findViewById(R.id.etFormula)
        etAdministration = findViewById(R.id.etAdministration)
        etStock = findViewById(R.id.etStock)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val productName = intent.getStringExtra("product_name") ?: "Producto"
        val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        toolbarTitle.text = "Editar $productName"
    }

    private fun loadProductData() {
        productMongoId = intent.getStringExtra("product_mongo_id") ?: ""
        etProductName.setText(intent.getStringExtra("product_name") ?: "")
        etExpiryDate.setText(intent.getStringExtra("product_expiry") ?: "")
        etDosage.setText(intent.getStringExtra("product_dose") ?: "")
        etPrice.setText(intent.getDoubleExtra("product_price", 0.0).toString())
        etIndications.setText(intent.getStringExtra("product_indications") ?: "")
        etFormula.setText(intent.getStringExtra("product_formula") ?: "")
        etAdministration.setText(intent.getStringExtra("product_administration") ?: "")
        etStock.setText(intent.getIntExtra("product_stock", 0).toString())
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }
        btnSaveToolbar.setOnClickListener { saveProduct() }
        btnSave.setOnClickListener { saveProduct() }
    }

    private fun saveProduct() {
        val name = etProductName.text.toString().trim()
        val expiryDate = etExpiryDate.text.toString().trim()
        val dosage = etDosage.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val indications = etIndications.text.toString().trim()
        val formula = etFormula.text.toString().trim()
        val administration = etAdministration.text.toString().trim()
        val stock = etStock.text.toString().trim()

        when {
            name.isEmpty() -> { Toast.makeText(this, "Ingresa el nombre del producto", Toast.LENGTH_SHORT).show(); return }
            price.isEmpty() -> { Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show(); return }
            expiryDate.isEmpty() -> { Toast.makeText(this, "Ingresa la fecha de caducidad", Toast.LENGTH_SHORT).show(); return }
            stock.isEmpty() -> { Toast.makeText(this, "Ingresa la existencia", Toast.LENGTH_SHORT).show(); return }
        }

        if (productMongoId.isEmpty()) {
            Toast.makeText(this, "Error: ID de producto no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val token = prefs.getString("token", "") ?: ""
        btnSave.isEnabled = false

        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "nombre" to name,
                    "fechaCaducidad" to expiryDate,
                    "dosis" to dosage,
                    "precio" to price,
                    "indicaciones" to indications,
                    "formula" to formula,
                    "administracion" to administration,
                    "existencia" to stock
                )
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.updateProduct(productMongoId, body)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditPaymentActivity, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditPaymentActivity, "Error al actualizar producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditPaymentActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnSave.isEnabled = true
            }
        }
    }
}