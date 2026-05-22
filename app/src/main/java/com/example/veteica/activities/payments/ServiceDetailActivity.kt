package com.example.veteica.activities.payments

import android.content.Intent  // 👈 AGREGA ESTA LÍNEA
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R

class ServiceDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: TextView
    private lateinit var btnSelect: Button
    private lateinit var btnCancel: Button
    private lateinit var tvServiceName: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var tvVeterinarians: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView

    private var serviceId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)

        initViews()
        setupToolbar()
        loadServiceData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        btnSelect = findViewById(R.id.btnSelect)
        btnCancel = findViewById(R.id.btnCancel)
        tvServiceName = findViewById(R.id.tvServiceName)
        tvQuantity = findViewById(R.id.tvQuantity)
        tvVeterinarians = findViewById(R.id.tvVeterinarians)
        tvPrice = findViewById(R.id.tvPrice)
        tvDescription = findViewById(R.id.tvDescription)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val serviceName = intent.getStringExtra("service_name") ?: "Servicio"
        val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        toolbarTitle.text = "Cobro de $serviceName"
    }

    private fun loadServiceData() {
        serviceId = intent.getIntExtra("service_id", 1)
        val serviceName = intent.getStringExtra("service_name") ?: "Citas"

        tvServiceName.text = serviceName

        when (serviceName) {
            "Citas" -> {
                tvQuantity.text = "15"
                tvVeterinarians.text = "2"
                tvPrice.text = "$100"
                tvDescription.text = "Consulta general para mascotas, incluye revisión y diagnóstico."
            }
            "Cirugías" -> {
                tvQuantity.text = "6"
                tvVeterinarians.text = "1"
                tvPrice.text = "$21,200"
                tvDescription.text = "Procedimientos quirúrgicos con anestesia y seguimiento postoperatorio."
            }
            "Observación" -> {
                tvQuantity.text = "10"
                tvVeterinarians.text = "1"
                tvPrice.text = "$600"
                tvDescription.text = "Monitoreo y cuidado de pacientes hospitalizados."
            }
            "Barro relajante" -> {
                tvQuantity.text = "7"
                tvVeterinarians.text = "1"
                tvPrice.text = "$2,000"
                tvDescription.text = "Terapia de relajación para mascotas con estrés o ansiedad."
            }
            else -> {
                tvQuantity.text = "0"
                tvVeterinarians.text = "0"
                tvPrice.text = "$0"
                tvDescription.text = "Descripción no disponible."
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditServiceActivity::class.java)
            intent.putExtra("service_id", serviceId)
            intent.putExtra("service_name", tvServiceName.text.toString())
            startActivity(intent)
        }

        btnSelect.setOnClickListener {
            Toast.makeText(this, "Servicio seleccionado para cobro", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}