package com.example.veteica.activities.owners

import android.app.AlertDialog
import android.app.Dialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.OwnerAppointmentAdapter
import com.example.veteica.adapters.SelectablePetAdapter
import com.example.veteica.adapters.OwnerEditPetAdapter
import com.example.veteica.models.OwnerAppointment
import com.example.veteica.models.Pet

class EditOwnerActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSaveToolbar: TextView
    private lateinit var btnSave: com.google.android.material.button.MaterialButton
    private lateinit var btnCancel: com.google.android.material.button.MaterialButton
    private lateinit var btnAddPet: com.google.android.material.button.MaterialButton
    private lateinit var layoutPhoto: android.widget.LinearLayout
    private lateinit var ivOwnerPhoto: ImageView
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var rvOwnerPets: RecyclerView
    private lateinit var rvAppointments: RecyclerView

    private lateinit var petAdapter: OwnerEditPetAdapter
    private val associatedPets = mutableListOf<Pet>()
    private val allPets = mutableListOf<Pet>()
    private var currentPhotoUri: Uri? = null

    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_owner)

        initViews()
        setupToolbar()
        setupRecyclerViews()
        loadMockData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSaveToolbar = findViewById(R.id.btnSaveToolbar)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnAddPet = findViewById(R.id.btnAddPet)
        layoutPhoto = findViewById(R.id.layoutPhoto)
        ivOwnerPhoto = findViewById(R.id.ivOwnerPhoto)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        rvOwnerPets = findViewById(R.id.rvOwnerPets)
        rvAppointments = findViewById(R.id.rvAppointments)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerViews() {
        // Adaptador para mascotas asociadas
        petAdapter = OwnerEditPetAdapter(associatedPets) { pet, position ->
            associatedPets.removeAt(position)
            petAdapter.updateList(associatedPets)
            Toast.makeText(this, "Mascota ${pet.name} eliminada", Toast.LENGTH_SHORT).show()
        }
        rvOwnerPets.layoutManager = LinearLayoutManager(this)
        rvOwnerPets.adapter = petAdapter
    }

    private fun loadMockData() {
        // Datos del dueño
        etName.setText("José Herrera")
        etPhone.setText("612-123-4567")
        etEmail.setText("jose@gmail.com")
        etAddress.setText("Colonia Púrpura #123")

        // Mascotas ya asociadas
        associatedPets.addAll(listOf(
            Pet(1, "Lilo", "Perro", "Labrador", 3, 28.5, "Macho", "Dorado", "José Herrera", ""),
            Pet(2, "Max", "Perro", "Bulldog", 5, 32.0, "Macho", "Atigrado", "José Herrera", ""),
            Pet(3, "Luna", "Gato", "Siames", 2, 4.2, "Hembra", "Blanco", "José Herrera", "")
        ))
        petAdapter.updateList(associatedPets)

        // Todas las mascotas disponibles (para seleccionar)
        allPets.addAll(listOf(
            Pet(1, "Lilo", "Perro", "Labrador", 3, 28.5, "Macho", "Dorado", "José Herrera", ""),
            Pet(2, "Max", "Perro", "Bulldog", 5, 32.0, "Macho", "Atigrado", "José Herrera", ""),
            Pet(3, "Luna", "Gato", "Siames", 2, 4.2, "Hembra", "Blanco", "José Herrera", ""),
            Pet(4, "Rocky", "Perro", "Pastor Alemán", 4, 35.0, "Macho", "Negro", "Otro Dueño", ""),
            Pet(5, "Bella", "Perro", "Poodle", 1, 6.5, "Hembra", "Blanco", "Otro Dueño", "")
        ))

        // Historial de citas
        val appointmentsList = listOf(
            OwnerAppointment(1, "Consulta general", "22/10/2025", "Ligero problema en el oído.", "Navarro admin"),
            OwnerAppointment(2, "Revisión", "23/10/2025", "Aun con ligero problema en el oído.", "Navarro admin"),
            OwnerAppointment(3, "Tratamiento", "24/10/2025", "Infección de oído", "Navarro admin")
        )

        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = OwnerAppointmentAdapter(appointmentsList) { appointment ->
            Toast.makeText(this, "Cita: ${appointment.consulta}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSelectPetDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_select_pet)
        dialog.setTitle("Seleccionar mascota")

        val rvSelectablePets = dialog.findViewById<RecyclerView>(R.id.rvSelectablePets)

        // Filtrar mascotas que no están ya asociadas
        val availablePets = allPets.filter { pet ->
            associatedPets.none { it.id == pet.id }
        }

        val selectableAdapter = SelectablePetAdapter(availablePets) { selectedPet ->
            associatedPets.add(selectedPet)
            petAdapter.updateList(associatedPets)
            Toast.makeText(this, "Mascota ${selectedPet.name} agregada", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        rvSelectablePets.layoutManager = LinearLayoutManager(this)
        rvSelectablePets.adapter = selectableAdapter

        dialog.show()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        btnAddPet.setOnClickListener {
            showSelectPetDialog()
        }

        layoutPhoto.setOnClickListener {
            showImagePickerDialog()
        }

        btnSaveToolbar.setOnClickListener { saveOwner() }
        btnSave.setOnClickListener { saveOwner() }
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
                        ivOwnerPhoto.setImageURI(uri)
                        ivOwnerPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivOwnerPhoto.setPadding(0, 0, 0, 0)
                        ivOwnerPhoto.setColorFilter(null)
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
                val message = "Dueño $name actualizado con ${associatedPets.size} mascota(s)"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}