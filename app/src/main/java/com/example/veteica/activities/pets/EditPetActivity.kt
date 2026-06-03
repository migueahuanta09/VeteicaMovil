package com.example.veteica.activities.pets

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class EditPetActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnUpdate: com.google.android.material.button.MaterialButton
    private lateinit var layoutPhoto: android.widget.LinearLayout
    private lateinit var ivPetPhoto: ImageView
    private lateinit var etName: EditText
    private lateinit var spinnerSpecies: Spinner
    private lateinit var etBreed: EditText
    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etColor: EditText
    private lateinit var etOwnerName: EditText
    private lateinit var etNotes: EditText
    private lateinit var prefs: SharedPreferences

    private var petMongoId: String = ""
    private var currentPhotoUri: Uri? = null
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
        initViews()
        setupToolbar()
        setupSpinners()
        loadPetData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnUpdate = findViewById(R.id.btnUpdate)
        layoutPhoto = findViewById(R.id.layoutPhoto)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etName = findViewById(R.id.etName)
        spinnerSpecies = findViewById(R.id.spinnerSpecies)
        etBreed = findViewById(R.id.etBreed)
        etAge = findViewById(R.id.etAge)
        etWeight = findViewById(R.id.etWeight)
        spinnerGender = findViewById(R.id.spinnerGender)
        etColor = findViewById(R.id.etColor)
        etOwnerName = findViewById(R.id.etOwnerName)
        etNotes = findViewById(R.id.etNotes)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinners() {
        val speciesList = arrayOf("Perro", "Gato", "Conejo", "Ave", "Reptil", "Otro")
        val speciesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speciesList)
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecies.adapter = speciesAdapter

        val genderList = arrayOf("Macho", "Hembra")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter
    }

    private fun loadPetData() {
        petMongoId = intent.getStringExtra("pet_mongo_id") ?: ""
        val petName = intent.getStringExtra("pet_name") ?: ""
        val petSpecies = intent.getStringExtra("pet_species") ?: ""
        val petBreed = intent.getStringExtra("pet_breed") ?: ""
        val petAge = intent.getIntExtra("pet_age", 0)
        val petWeight = intent.getDoubleExtra("pet_weight", 0.0)
        val petGender = intent.getStringExtra("pet_gender") ?: ""
        val petColor = intent.getStringExtra("pet_color") ?: ""
        val petOwnerName = intent.getStringExtra("pet_owner_name") ?: ""
        val petNotes = intent.getStringExtra("pet_notes") ?: ""

        etName.setText(petName)
        etBreed.setText(petBreed)
        etAge.setText(petAge.toString())
        etWeight.setText(petWeight.toString())
        etColor.setText(petColor)
        etOwnerName.setText(petOwnerName)
        etNotes.setText(petNotes)

        val speciesList = arrayOf("Perro", "Gato", "Conejo", "Ave", "Reptil", "Otro")
        val speciesIndex = speciesList.indexOfFirst { it.equals(petSpecies, ignoreCase = true) }
        if (speciesIndex >= 0) spinnerSpecies.setSelection(speciesIndex)

        val genderList = arrayOf("Macho", "Hembra")
        val genderIndex = genderList.indexOfFirst { it.equals(petGender, ignoreCase = true) }
        if (genderIndex >= 0) spinnerGender.setSelection(genderIndex)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        layoutPhoto.setOnClickListener { showImagePickerDialog() }
        btnSaveToolbar.setOnClickListener { updatePet() }
        btnUpdate.setOnClickListener { updatePet() }
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
        if (hasPermissions) openCamera()
        else ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            createImageUri()?.let {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) openCamera()
            else Toast.makeText(this, "Se necesitan permisos para usar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri = if (requestCode == REQUEST_CODE_CAMERA) currentPhotoUri else data?.data
            uri?.let {
                currentPhotoUri = it
                ivPetPhoto.setImageURI(it)
                ivPetPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                ivPetPhoto.setPadding(0, 0, 0, 0)
                ivPetPhoto.setColorFilter(null)
            }
        }
    }

    private fun updatePet() {
        val name = etName.text.toString().trim()
        val species = spinnerSpecies.selectedItem.toString()
        val breed = etBreed.text.toString().trim()
        val age = etAge.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()
        val color = etColor.text.toString().trim()
        val ownerName = etOwnerName.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        when {
            name.isEmpty() -> { Toast.makeText(this, "Ingresa el nombre de la mascota", Toast.LENGTH_SHORT).show(); return }
            breed.isEmpty() -> { Toast.makeText(this, "Ingresa la raza", Toast.LENGTH_SHORT).show(); return }
            age.isEmpty() -> { Toast.makeText(this, "Ingresa la edad", Toast.LENGTH_SHORT).show(); return }
            ownerName.isEmpty() -> { Toast.makeText(this, "Ingresa el nombre del dueño", Toast.LENGTH_SHORT).show(); return }
        }

        if (petMongoId.isEmpty()) {
            Toast.makeText(this, "Error: ID de mascota no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val token = prefs.getString("token", "") ?: ""
        btnUpdate.isEnabled = false
        btnUpdate.text = "Guardando..."

        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "nombre" to name,
                    "especie" to species,
                    "raza" to breed,
                    "edad" to age,
                    "peso" to weight,
                    "sexo" to gender,
                    "color" to color,
                    "nombreDueno" to ownerName,
                    "notas" to notes
                )
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.updatePet(petMongoId, body)

                if (response.isSuccessful) {
                    Toast.makeText(this@EditPetActivity, "Mascota $name actualizada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditPetActivity, "Error al actualizar mascota", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditPetActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnUpdate.isEnabled = true
                btnUpdate.text = "ACTUALIZAR"
            }
        }
    }
}