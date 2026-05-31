package com.example.veteica.activities.profile

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.veteica.R
import com.example.veteica.activities.auth.LoginActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: com.google.android.material.button.MaterialButton
    private lateinit var btnLogout: com.google.android.material.button.MaterialButton
    private lateinit var layoutPhoto: android.widget.LinearLayout
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etCedula: EditText
    private lateinit var etSpecialty: EditText

    // Variables para la foto
    private var currentPhotoPath: String = ""
    private var selectedImageUri: Uri? = null
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        btnLogout = findViewById(R.id.btnLogout)
        layoutPhoto = findViewById(R.id.layoutPhoto)
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etCedula = findViewById(R.id.etCedula)
        etSpecialty = findViewById(R.id.etSpecialty)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun loadUserData() {
        val fullName = "Dra. María González"
        val email = "dra.maria@veteica.com"
        val phone = "612-123-4567"
        val cedula = "VET-12345-6789"
        val specialty = "Medicina Interna"

        etFullName.setText(fullName)
        etEmail.setText(email)
        etPhone.setText(phone)
        etCedula.setText(cedula)
        etSpecialty.setText(specialty)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        layoutPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        btnSave.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val specialty = etSpecialty.text.toString().trim()

            when {
                fullName.isEmpty() -> Toast.makeText(this, "Ingresa tu nombre completo", Toast.LENGTH_SHORT).show()
                email.isEmpty() -> Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                phone.isEmpty() -> Toast.makeText(this, "Ingresa tu teléfono", Toast.LENGTH_SHORT).show()
                cedula.isEmpty() -> Toast.makeText(this, "Ingresa tu cédula profesional", Toast.LENGTH_SHORT).show()
                specialty.isEmpty() -> Toast.makeText(this, "Ingresa tu especialidad", Toast.LENGTH_SHORT).show()
                else -> {
                    Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de galería", "Cancelar")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar foto")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkPermissionsAndOpenCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkPermissionsAndOpenCamera() {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (hasPermissions) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = createImageFile()
            photoFile?.let {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(null)
        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openCamera()
            } else {
                Toast.makeText(this, "Se necesitan permisos para usar la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    val file = File(currentPhotoPath)
                    if (file.exists()) {
                        selectedImageUri = Uri.fromFile(file)
                        ivProfilePhoto.setImageURI(selectedImageUri)
                        ivProfilePhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivProfilePhoto.visibility = android.view.View.VISIBLE
                        ivProfilePhoto.setPadding(0, 0, 0, 0)
                        ivProfilePhoto.setColorFilter(null)
                    }
                }
                REQUEST_CODE_GALLERY -> {
                    selectedImageUri = data?.data
                    selectedImageUri?.let {
                        ivProfilePhoto.setImageURI(it)
                        ivProfilePhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivProfilePhoto.visibility = android.view.View.VISIBLE
                        ivProfilePhoto.setPadding(0, 0, 0, 0)
                        ivProfilePhoto.setColorFilter(null)
                    }
                }
            }
        }
    }
}