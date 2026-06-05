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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePetActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnCreate: com.google.android.material.button.MaterialButton
    private lateinit var layoutPhoto: android.widget.LinearLayout
    private lateinit var ivPetPhoto: ImageView
    private lateinit var etName: EditText
    private lateinit var spinnerSpecies: Spinner
    private lateinit var spinnerBreed: Spinner
    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var tvOwnerSelected: TextView
    private lateinit var btnSelectOwner: com.google.android.material.button.MaterialButton
    private lateinit var etNotes: EditText
    private lateinit var sharedPreferences: SharedPreferences

    // Map de razas por especie
    private val breedsMap = mapOf(
        "Perro" to listOf("Labrador", "Pastor Alemán", "Bulldog", "Poodle", "Chihuahua", "Golden Retriever", "Boxer", "Dálmata", "Husky", "Otro"),
        "Gato" to listOf("Persa", "Siamés", "Maine Coon", "Bengala", "Esfinge", "Angora", "Bosque de Noruega", "Otro"),
        "Conejo" to listOf("Belier", "Enano", "Angora", "Rex", "Cabeza de León", "Otro"),
        "Ave" to listOf("Perico", "Loro", "Canario", "Cacatúa", "Agapornis", "Otro"),
        "Reptil" to listOf("Iguana", "Serpiente", "Tortuga", "Gecko", "Camaleón", "Otro"),
        "Otro" to listOf("Otro")
    )

    private var currentPhotoUri: Uri? = null
    private var selectedOwnerId: String = ""
    private var selectedOwnerName: String = ""
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pet)

        sharedPreferences = getSharedPreferences("veteica_prefs", MODE_PRIVATE)

        initViews()
        setupToolbar()
        setupSpinners()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnCreate = findViewById(R.id.btnCreate)
        layoutPhoto = findViewById(R.id.layoutPhoto)
        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etName = findViewById(R.id.etName)
        spinnerSpecies = findViewById(R.id.spinnerSpecies)
        spinnerBreed = findViewById(R.id.spinnerBreed)
        etAge = findViewById(R.id.etAge)
        etWeight = findViewById(R.id.etWeight)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerColor = findViewById(R.id.spinnerColor)
        tvOwnerSelected = findViewById(R.id.tvOwnerSelected)
        btnSelectOwner = findViewById(R.id.btnSelectOwner)
        etNotes = findViewById(R.id.etNotes)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSpinners() {
        val speciesList = listOf("Perro", "Gato", "Conejo", "Ave", "Reptil", "Otro")
        val speciesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speciesList)
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecies.adapter = speciesAdapter

        val genderList = listOf("Macho", "Hembra")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        val colorList = listOf("Blanco", "Negro", "Café", "Gris", "Dorado", "Atigrado", "Manchas", "Otro")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorList)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = colorAdapter

        spinnerSpecies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedSpecies = spinnerSpecies.selectedItem.toString()
                updateBreedsSpinner(selectedSpecies)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateBreedsSpinner(species: String) {
        val breeds = breedsMap[species] ?: listOf("Otro")
        val breedAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, breeds)
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBreed.adapter = breedAdapter
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        layoutPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        btnSelectOwner.setOnClickListener {
            showSelectOwnerDialog()
        }

        btnCreate.setOnClickListener {
            savePet()
        }
    }

    private fun showSelectOwnerDialog() {
        val token = sharedPreferences.getString("token", "") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getOwners()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val success = body?.get("success") as? Boolean ?: false

                        if (success) {
                            val data = body?.get("data") as? Map<*, *>
                            val items = data?.get("items") as? List<*>

                            val ownerNames = mutableListOf<Pair<String, String>>()

                            items?.forEach { item ->
                                val ownerMap = item as? Map<*, *>
                                if (ownerMap != null) {
                                    val id = ownerMap["_id"] as? String ?: ""
                                    val name = ownerMap["nombre"] as? String ?: ""
                                    if (id.isNotEmpty() && name.isNotEmpty()) {
                                        ownerNames.add(Pair(id, name))
                                    }
                                }
                            }

                            if (ownerNames.isEmpty()) {
                                Toast.makeText(this@CreatePetActivity, "No hay dueños registrados", Toast.LENGTH_SHORT).show()
                                return@withContext
                            }

                            val options = ownerNames.map { it.second }.toTypedArray()
                            AlertDialog.Builder(this@CreatePetActivity)
                                .setTitle("Seleccionar Dueño")
                                .setItems(options) { _, which ->
                                    selectedOwnerId = ownerNames[which].first
                                    selectedOwnerName = ownerNames[which].second
                                    tvOwnerSelected.text = selectedOwnerName
                                    tvOwnerSelected.setTextColor(resources.getColor(R.color.veteica_green))
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        } else {
                            Toast.makeText(this@CreatePetActivity, "Error al cargar dueños", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@CreatePetActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreatePetActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
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
        if (hasPermissions) openCamera()
        else ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
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
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            openCamera()
        } else {
            Toast.makeText(this, "Se necesitan permisos para usar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    currentPhotoUri?.let { uri ->
                        ivPetPhoto.setImageURI(uri)
                        ivPetPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivPetPhoto.setPadding(0, 0, 0, 0)
                        ivPetPhoto.setColorFilter(null)
                    }
                }
                REQUEST_CODE_GALLERY -> {
                    val uri = data?.data
                    uri?.let {
                        currentPhotoUri = it
                        ivPetPhoto.setImageURI(it)
                        ivPetPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivPetPhoto.setPadding(0, 0, 0, 0)
                        ivPetPhoto.setColorFilter(null)
                    }
                }
            }
        }
    }

    private fun savePet() {
        val name = etName.text.toString().trim()
        val species = spinnerSpecies.selectedItem.toString()
        val breed = spinnerBreed.selectedItem.toString()
        val age = etAge.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()
        val color = spinnerColor.selectedItem.toString()
        val notes = etNotes.text.toString().trim()

        when {
            name.isEmpty() -> Toast.makeText(this, "Ingresa el nombre de la mascota", Toast.LENGTH_SHORT).show()
            age.isEmpty() -> Toast.makeText(this, "Ingresa la edad", Toast.LENGTH_SHORT).show()
            selectedOwnerId.isEmpty() -> Toast.makeText(this, "Selecciona un dueño", Toast.LENGTH_SHORT).show()
            else -> {
                val token = sharedPreferences.getString("token", "") ?: ""
                lifecycleScope.launch {
                    try {
                        val api = RetrofitClient.instanceWithToken(token)
                        val body = mapOf(
                            "nombre" to name,
                            "especie" to species,
                            "raza" to breed,
                            "edad" to age,
                            "peso" to weight,
                            "genero" to gender,
                            "color" to color,
                            "ownerId" to selectedOwnerId,
                            "nombreDueno" to selectedOwnerName,  // ← CAMPO REQUERIDO
                            "notas" to notes
                        )
                        val response = api.createPet(body)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@CreatePetActivity, "Paciente $name creado exitosamente", Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(this@CreatePetActivity, "Error al crear paciente: $errorBody", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreatePetActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}