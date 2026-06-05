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

class EditPetActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnUpdate: com.google.android.material.button.MaterialButton
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

    private val breedsMap = mapOf(
        "Perro" to listOf("Labrador", "Pastor Alemán", "Bulldog", "Poodle", "Chihuahua", "Golden Retriever", "Boxer", "Dálmata", "Husky", "Otro"),
        "Gato" to listOf("Persa", "Siamés", "Maine Coon", "Bengala", "Esfinge", "Angora", "Bosque de Noruega", "Otro"),
        "Conejo" to listOf("Belier", "Enano", "Angora", "Rex", "Cabeza de León", "Otro"),
        "Ave" to listOf("Perico", "Loro", "Canario", "Cacatúa", "Agapornis", "Otro"),
        "Reptil" to listOf("Iguana", "Serpiente", "Tortuga", "Gecko", "Camaleón", "Otro"),
        "Otro" to listOf("Otro")
    )

    private var petId: String = ""
    private var currentPhotoUri: Uri? = null
    private var selectedOwnerId: String = ""
    private var selectedOwnerName: String = ""
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)

        sharedPreferences = getSharedPreferences("veteica_prefs", MODE_PRIVATE)

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

    private fun loadPetData() {
        petId = intent.getStringExtra("pet_id") ?: ""
        val token = sharedPreferences.getString("token", "") ?: ""

        if (petId.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getPet(petId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val success = body?.get("success") as? Boolean ?: false

                        if (success) {
                            val data = body?.get("data") as? Map<*, *>
                            if (data != null) {
                                val name = data["nombre"] as? String ?: ""
                                val species = data["especie"] as? String ?: "Perro"
                                val breed = data["raza"] as? String ?: ""
                                val age = (data["edad"] as? Number)?.toInt() ?: 0
                                val weight = (data["peso"] as? Number)?.toDouble() ?: 0.0
                                val gender = data["genero"] as? String ?: "Macho"
                                val color = data["color"] as? String ?: "Blanco"
                                val notes = data["notas"] as? String ?: ""
                                val ownerId = data["ownerId"] as? String ?: ""
                                val ownerName = data["nombreDueno"] as? String ?: ""

                                etName.setText(name)
                                etAge.setText(age.toString())
                                etWeight.setText(weight.toString())
                                etNotes.setText(notes)

                                val speciesPosition = listOf("Perro", "Gato", "Conejo", "Ave", "Reptil", "Otro").indexOf(species)
                                if (speciesPosition >= 0) spinnerSpecies.setSelection(speciesPosition)

                                updateBreedsSpinner(species)
                                val breedsList = breedsMap[species] ?: listOf("Otro")
                                val breedPosition = breedsList.indexOf(breed)
                                if (breedPosition >= 0) spinnerBreed.setSelection(breedPosition)

                                val genderPosition = listOf("Macho", "Hembra").indexOf(gender)
                                if (genderPosition >= 0) spinnerGender.setSelection(genderPosition)

                                val colorPosition = listOf("Blanco", "Negro", "Café", "Gris", "Dorado", "Atigrado", "Manchas", "Otro").indexOf(color)
                                if (colorPosition >= 0) spinnerColor.setSelection(colorPosition)

                                if (ownerId.isNotEmpty()) {
                                    selectedOwnerId = ownerId
                                    selectedOwnerName = ownerName
                                    tvOwnerSelected.text = ownerName
                                    tvOwnerSelected.setTextColor(resources.getColor(R.color.veteica_green))
                                } else if (ownerName.isNotEmpty()) {
                                    selectedOwnerName = ownerName
                                    tvOwnerSelected.text = ownerName
                                    tvOwnerSelected.setTextColor(resources.getColor(R.color.veteica_green))
                                } else {
                                    tvOwnerSelected.text = "Ningún dueño seleccionado"
                                    tvOwnerSelected.setTextColor(resources.getColor(R.color.veteica_gray))
                                }
                            }
                        } else {
                            Toast.makeText(this@EditPetActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EditPetActivity, "Error de conexión: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditPetActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        layoutPhoto.setOnClickListener { showImagePickerDialog() }
        btnSelectOwner.setOnClickListener { showSelectOwnerDialog() }
        btnSaveToolbar.setOnClickListener { updatePet() }
        btnUpdate.setOnClickListener { updatePet() }
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
                                Toast.makeText(this@EditPetActivity, "No hay dueños registrados", Toast.LENGTH_SHORT).show()
                                return@withContext
                            }

                            val options = ownerNames.map { it.second }.toTypedArray()
                            AlertDialog.Builder(this@EditPetActivity)
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
                            Toast.makeText(this@EditPetActivity, "Error al cargar dueños", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EditPetActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditPetActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun updatePet() {
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
            selectedOwnerId.isEmpty() && selectedOwnerName.isEmpty() -> Toast.makeText(this, "Selecciona un dueño", Toast.LENGTH_SHORT).show()
            else -> {
                val token = sharedPreferences.getString("token", "") ?: ""
                lifecycleScope.launch {
                    try {
                        val api = RetrofitClient.instanceWithToken(token)
                        val body = mutableMapOf<String, String>(
                            "nombre" to name,
                            "especie" to species,
                            "raza" to breed,
                            "edad" to age,
                            "peso" to weight,
                            "genero" to gender,
                            "color" to color,
                            "notas" to notes
                        )
                        if (selectedOwnerId.isNotEmpty()) {
                            body["ownerId"] = selectedOwnerId
                            body["nombreDueno"] = selectedOwnerName
                        } else if (selectedOwnerName.isNotEmpty()) {
                            body["nombreDueno"] = selectedOwnerName
                        }

                        val response = api.updatePet(petId, body)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@EditPetActivity, "Paciente $name actualizado correctamente", Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(this@EditPetActivity, "Error al actualizar: $errorBody", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditPetActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}