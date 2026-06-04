package com.example.veteica.activities.appointments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: TextView
    private lateinit var btnComplete: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var ivPetPhoto: ImageView
    private lateinit var tvPetName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvOwnerName: TextView
    private lateinit var tvVeterinarian: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvReason: TextView
    private lateinit var tvDiagnosis: TextView
    private lateinit var prefs: SharedPreferences

    private var appointmentId: Int = 0
    private var appointmentMongoId: String = ""
    private var appointmentPetName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_detail)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        loadData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)
        btnComplete = findViewById(R.id.btnComplete)
        btnCancel = findViewById(R.id.btnCancel)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        tvPetName = findViewById(R.id.tvPetName)
        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)
        tvOwnerName = findViewById(R.id.tvOwnerName)
        tvVeterinarian = findViewById(R.id.tvVeterinarian)
        tvStatus = findViewById(R.id.tvStatus)
        tvReason = findViewById(R.id.tvReason)
        tvDiagnosis = findViewById(R.id.tvDiagnosis)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadData() {
        appointmentId = intent.getIntExtra("appointment_id", 0)
        appointmentMongoId = intent.getStringExtra("appointment_mongo_id") ?: ""
        appointmentPetName = intent.getStringExtra("appointment_pet") ?: ""

        tvPetName.text = appointmentPetName
        tvDate.text = intent.getStringExtra("appointment_date") ?: ""
        tvTime.text = intent.getStringExtra("appointment_time") ?: ""
        tvOwnerName.text = intent.getStringExtra("appointment_owner") ?: ""
        tvVeterinarian.text = intent.getStringExtra("appointment_vet") ?: ""
        tvReason.text = intent.getStringExtra("appointment_reason") ?: ""
        tvDiagnosis.text = intent.getStringExtra("appointment_diagnosis") ?: ""

        val status = intent.getStringExtra("appointment_status") ?: "Pendiente"
        updateStatusUI(status)
    }

    private fun updateStatusUI(status: String) {
        tvStatus.text = status
        when (status) {
            "Pendiente" -> tvStatus.setBackgroundResource(R.drawable.bg_status_orange)
            "Confirmada" -> tvStatus.setBackgroundResource(R.drawable.bg_status_blue)
            "Completada" -> {
                tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                btnComplete.isEnabled = false
                btnComplete.text = "Cita completada"
            }
            "Cancelada" -> {
                tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                btnCancel.isEnabled = false
                btnComplete.isEnabled = false
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditAppointmentActivity::class.java)
            intent.putExtra("appointment_id", appointmentId)
            intent.putExtra("appointment_mongo_id", appointmentMongoId)
            startActivity(intent)
        }

        btnComplete.setOnClickListener {
            if (appointmentMongoId.isEmpty()) {
                Toast.makeText(this, "Error: ID de cita no encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val token = prefs.getString("token", "") ?: ""
            btnComplete.isEnabled = false
            btnComplete.text = "Guardando..."

            lifecycleScope.launch {
                try {
                    val api = RetrofitClient.instanceWithToken(token)
                    val response = api.completeAppointment(appointmentMongoId)
                    if (response.isSuccessful) {
                        updateStatusUI("Completada")
                        Toast.makeText(this@AppointmentDetailActivity, "Cita completada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AppointmentDetailActivity, "Error al completar cita", Toast.LENGTH_SHORT).show()
                        btnComplete.isEnabled = true
                        btnComplete.text = "COMPLETAR"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AppointmentDetailActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    btnComplete.isEnabled = true
                    btnComplete.text = "COMPLETAR"
                }
            }
        }

        btnCancel.setOnClickListener {
            if (appointmentMongoId.isEmpty()) {
                Toast.makeText(this, "Error: ID de cita no encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val token = prefs.getString("token", "") ?: ""
            btnCancel.isEnabled = false

            lifecycleScope.launch {
                try {
                    val api = RetrofitClient.instanceWithToken(token)
                    val response = api.cancelAppointment(appointmentMongoId)
                    if (response.isSuccessful) {
                        updateStatusUI("Cancelada")
                        Toast.makeText(this@AppointmentDetailActivity, "Cita cancelada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AppointmentDetailActivity, "Error al cancelar cita", Toast.LENGTH_SHORT).show()
                        btnCancel.isEnabled = true
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AppointmentDetailActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    btnCancel.isEnabled = true
                }
            }
        }
    }
}