package com.example.veteica.activities.payments

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class PendingPaymentDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnCharge: Button
    private lateinit var btnGeneratePdf: Button
    private lateinit var tvPetName: TextView
    private lateinit var tvServiceName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvPaymentId: TextView
    private lateinit var prefs: SharedPreferences

    private var paymentId: Int = 0
    private var paymentMongoId: String = ""
    private var petName: String = ""
    private var serviceName: String = ""
    private var date: String = ""
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_payment_detail)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        loadData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCharge = findViewById(R.id.btnCharge)
        btnGeneratePdf = findViewById(R.id.btnGeneratePdf)
        tvPetName = findViewById(R.id.tvPetName)
        tvServiceName = findViewById(R.id.tvServiceName)
        tvDate = findViewById(R.id.tvDate)
        tvTotal = findViewById(R.id.tvTotal)
        tvStatus = findViewById(R.id.tvStatus)
        tvPaymentId = findViewById(R.id.tvPaymentId)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadData() {
        paymentId = intent.getIntExtra("payment_id", 1)
        paymentMongoId = intent.getStringExtra("payment_mongo_id") ?: ""
        petName = intent.getStringExtra("payment_pet") ?: ""
        serviceName = intent.getStringExtra("payment_service") ?: "Consulta general"
        date = intent.getStringExtra("payment_date") ?: ""
        total = intent.getDoubleExtra("payment_total", 0.0)

        val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        tvPaymentId.text = "Cobro #$paymentId"
        tvPetName.text = petName
        tvServiceName.text = serviceName
        tvDate.text = date
        tvTotal.text = format.format(total)
        tvStatus.text = "Pendiente"
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnCharge.setOnClickListener {
            if (paymentMongoId.isEmpty()) {
                Toast.makeText(this, "Error: ID de pago no encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = prefs.getString("token", "") ?: ""
            btnCharge.isEnabled = false
            btnCharge.text = "Cobrando..."

            lifecycleScope.launch {
                try {
                    val body = mapOf(
                        "metodoPago" to "Efectivo",
                        "montoPagado" to total.toString()
                    )
                    val api = RetrofitClient.instanceWithToken(token)
                    val response = api.chargePayment(paymentMongoId, body)

                    if (response.isSuccessful) {
                        tvStatus.text = "Cobrado"
                        tvStatus.setTextColor(resources.getColor(R.color.veteica_green))
                        btnCharge.text = "Cobrado"
                        Toast.makeText(this@PendingPaymentDetailActivity, "Cobro realizado correctamente", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@PendingPaymentDetailActivity, "Error al realizar cobro", Toast.LENGTH_SHORT).show()
                        btnCharge.isEnabled = true
                        btnCharge.text = "COBRAR"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PendingPaymentDetailActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    btnCharge.isEnabled = true
                    btnCharge.text = "COBRAR"
                }
            }
        }

        btnGeneratePdf.setOnClickListener {
            Toast.makeText(this, "Generando ticket para $petName", Toast.LENGTH_LONG).show()
        }
    }
}