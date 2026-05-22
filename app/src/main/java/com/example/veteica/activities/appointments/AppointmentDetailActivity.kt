package com.example.veteica.activities.appointments

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.example.veteica.R

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

    private var appointmentId: Int = 0
    private var appointmentPetName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_detail)

        initViews()
        setupToolbar()
        loadMockData()
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

    private fun loadMockData() {
        appointmentId = intent.getIntExtra("appointment_id", 1)
        appointmentPetName = intent.getStringExtra("appointment_pet") ?: "Lilo"

        tvPetName.text = appointmentPetName
        tvDate.text = "20/10/2025"
        tvTime.text = "10:00 AM"
        tvOwnerName.text = "Jose Herrera"
        tvVeterinarian.text = "Navarro admin"
        tvReason.text = "Ligero problema en el oído."
        tvDiagnosis.text = "Infección en el oído."

        updateStatusUI("Pendiente")
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
            "Revisado" -> tvStatus.setBackgroundResource(R.drawable.bg_status_green)
            "Cancelada" -> {
                tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                btnCancel.isEnabled = false
                btnComplete.isEnabled = false
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditAppointmentActivity::class.java)
            intent.putExtra("appointment_id", appointmentId)
            startActivity(intent)
        }

        btnComplete.setOnClickListener {
            Toast.makeText(this, "Cita marcada como Completada", Toast.LENGTH_SHORT).show()
            updateStatusUI("Completada")

            // Enviar a cobros pendientes
            val message = "La cita de $appointmentPetName ha sido agregada a la lista de cobros pendientes"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        btnCancel.setOnClickListener {
            Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()
            updateStatusUI("Cancelada")
        }
    }
}