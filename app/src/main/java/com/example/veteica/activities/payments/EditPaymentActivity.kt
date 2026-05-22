package com.example.veteica.activities.payments

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

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

    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_payment)

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
        productId = intent.getIntExtra("product_id", 1)
        val productName = intent.getStringExtra("product_name") ?: "Nobivac"

        // Cargar datos mock del producto
        etProductName.setText(productName)
        etExpiryDate.setText("20/10/2030")
        etDosage.setText("1 dosis")
        etPrice.setText("1000")
        etIndications.setText("Para la inmunización activa contra la rabia de perros, gatos y turones.")
        etFormula.setText("Cultivo del virus de la rabia, clonado de la cepa Pasteur RIVM")
        etAdministration.setText("Por vía subcutánea o intramuscular.")
        etStock.setText("10")
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSaveToolbar.setOnClickListener {
            saveProduct()
        }

        btnSave.setOnClickListener {
            saveProduct()
        }
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
            name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del producto", Toast.LENGTH_SHORT).show()
            expiryDate.isEmpty() -> Toast.makeText(this, "Ingresa la fecha de caducidad", Toast.LENGTH_SHORT).show()
            dosage.isEmpty() -> Toast.makeText(this, "Ingresa la dosis", Toast.LENGTH_SHORT).show()
            price.isEmpty() -> Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show()
            stock.isEmpty() -> Toast.makeText(this, "Ingresa la existencia", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Producto $name actualizado correctamente", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}