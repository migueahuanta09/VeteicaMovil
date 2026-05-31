package com.example.veteica.activities.owners

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import com.example.veteica.R

class CreateOwnerActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnCreate: com.google.android.material.button.MaterialButton
    private lateinit var layoutPhoto: android.widget.LinearLayout
    private lateinit var ivOwnerPhoto: ImageView
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText

    private var currentPhotoUri: Uri? = null
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_owner)

        initViews()
        setupToolbar()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnCreate = findViewById(R.id.btnCreate)
        layoutPhoto = findViewById(R.id.layoutPhoto)
        ivOwnerPhoto = findViewById(R.id.ivOwnerPhoto)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        layoutPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        btnSaveToolbar.setOnClickListener {
            saveOwner()
        }

        btnCreate.setOnClickListener {
            saveOwner()
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
        val permissions = arrayOf(android.Manifest.permission.CAMERA)

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
            val photoUri = createImageUri()
            photoUri?.let {
                currentPhotoUri = it
                intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "JPEG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Veteica")
            }
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
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
                    currentPhotoUri?.let { uri ->
                        ivOwnerPhoto.setImageURI(uri)
                        ivOwnerPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivOwnerPhoto.setPadding(0, 0, 0, 0)
                        ivOwnerPhoto.setColorFilter(null)
                        ivOwnerPhoto.setBackgroundColor(0)
                    }
                }
                REQUEST_CODE_GALLERY -> {
                    val uri = data?.data
                    uri?.let {
                        currentPhotoUri = it
                        ivOwnerPhoto.setImageURI(it)
                        ivOwnerPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivOwnerPhoto.setPadding(0, 0, 0, 0)
                        ivOwnerPhoto.setColorFilter(null)
                        ivOwnerPhoto.setBackgroundColor(0)
                    }
                }
            }
        }
    }

    private fun saveOwner() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()

        when {
            name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre del dueño", Toast.LENGTH_SHORT).show()
            phone.isEmpty() -> Toast.makeText(this, "Ingresa el teléfono", Toast.LENGTH_SHORT).show()
            email.isEmpty() -> Toast.makeText(this, "Ingresa el correo electrónico", Toast.LENGTH_SHORT).show()
            address.isEmpty() -> Toast.makeText(this, "Ingresa la dirección", Toast.LENGTH_SHORT).show()
            else -> {
                Toast.makeText(this, "Dueño $name creado exitosamente", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}