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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veteica.R
import com.example.veteica.adapters.OwnerAppointmentAdapter
import com.example.veteica.adapters.OwnerEditPetAdapter
import com.example.veteica.adapters.SelectablePetAdapter
import com.example.veteica.models.OwnerAppointment
import com.example.veteica.models.Pet
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

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
    private var ownerId: String = ""

    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val REQUEST_CODE_PERMISSIONS = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_owner)

        initViews()
        setupToolbar()
        setupRecyclerViews()
        loadOwnerData()
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
        petAdapter = OwnerEditPetAdapter(associatedPets) { pet, position ->
            associatedPets.removeAt(position)
            petAdapter.updateList(associatedPets)
            Toast.makeText(this, "Mascota ${pet.name} eliminada", Toast.LENGTH_SHORT).show()
        }
        rvOwnerPets.layoutManager = LinearLayoutManager(this)
        rvOwnerPets.adapter = petAdapter
    }

    private fun loadOwnerData() {
        ownerId = intent.getStringExtra("owner_mongo_id") ?: ""
        val ownerName = intent.getStringExtra("owner_name") ?: ""

        etName.setText(ownerName)
        etPhone.setText(intent.getStringExtra("owner_phone") ?: "")
        etEmail.setText(intent.getStringExtra("owner_email") ?: "")
        etAddress.setText(intent.getStringExtra("owner_address") ?: "")

        // Historial de citas mock por ahora
        val appointmentsList = listOf(
            OwnerAppointment(1, "Consulta general", "22/10/2025", "Ligero problema en el oído.", "Navarro admin"),
            OwnerAppointment(2, "Revisión", "23/10/2025", "Aun con ligero problema en el oído.", "Navarro admin"),
            OwnerAppointment(3, "Tratamiento", "24/10/2025", "Infección de oído", "Navarro admin")
        )
        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = OwnerAppointmentAdapter(appointmentsList) { appointment ->
            Toast.makeText(this, "Cita: ${appointment.consulta}", Toast.LENGTH_SHORT).show()
        }

        loadPetsFromBackend()
    }

    private fun loadPetsFromBackend() {
        val token = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
            .getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.getPets()

                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    val items = data?.get("items") as? List<*>

                    allPets.clear()
                    items?.forEachIndexed { index, item ->
                        val pet = item as? Map<*, *> ?: return@forEachIndexed
                        allPets.add(Pet(
                            id = index + 1,
                            mongoId = pet["_id"] as? String ?: "",
                            name = pet["nombre"] as? String ?: "",
                            species = pet["especie"] as? String ?: "",
                            breed = pet["raza"] as? String ?: "",
                            age = (pet["edad"] as? Double)?.toInt() ?: 0,
                            weight = pet["peso"] as? Double ?: 0.0,
                            gender = pet["genero"] as? String ?: "",
                            color = pet["color"] as? String ?: "",
                            ownerName = pet["nombreDueno"] as? String ?: "",
                            notes = pet["notas"] as? String ?: ""
                        ))
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditOwnerActivity, "Error cargando mascotas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSelectPetDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_select_pet)

        val rvSelectablePets = dialog.findViewById<RecyclerView>(R.id.rvSelectablePets)

        val availablePets = allPets.filter { pet ->
            associatedPets.none { it.mongoId == pet.mongoId }
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
        btnAddPet.setOnClickListener { showSelectPetDialog() }
        layoutPhoto.setOnClickListener { showImagePickerDialog() }
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
            }.show()
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
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            openCamera()
        } else {
            Toast.makeText(this, "Se necesitan permisos para la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> currentPhotoUri?.let {
                    ivOwnerPhoto.setImageURI(it)
                    ivOwnerPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivOwnerPhoto.setPadding(0, 0, 0, 0)
                    ivOwnerPhoto.setColorFilter(null)
                }
                REQUEST_CODE_GALLERY -> data?.data?.let {
                    currentPhotoUri = it
                    ivOwnerPhoto.setImageURI(it)
                    ivOwnerPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivOwnerPhoto.setPadding(0, 0, 0, 0)
                    ivOwnerPhoto.setColorFilter(null)
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
            name.isEmpty() -> { Toast.makeText(this, "Ingresa el nombre", Toast.LENGTH_SHORT).show(); return }
            phone.isEmpty() -> { Toast.makeText(this, "Ingresa el teléfono", Toast.LENGTH_SHORT).show(); return }
            email.isEmpty() -> { Toast.makeText(this, "Ingresa el correo", Toast.LENGTH_SHORT).show(); return }
            address.isEmpty() -> { Toast.makeText(this, "Ingresa la dirección", Toast.LENGTH_SHORT).show(); return }
        }

        if (ownerId.isEmpty()) {
            Toast.makeText(this, "Error: no se encontró el ID del dueño", Toast.LENGTH_SHORT).show()
            return
        }

        val token = getSharedPreferences("veteica_prefs", MODE_PRIVATE)
            .getString("token", "") ?: ""

        btnSave.isEnabled = false
        btnSave.text = "Guardando..."

        lifecycleScope.launch {
            try {
                val body = mapOf(
                    "nombre" to name,
                    "telefono" to phone,
                    "email" to email,
                    "direccion" to address
                )
                val api = RetrofitClient.instanceWithToken(token)
                val response = api.updateOwner(ownerId, body)

                if (response.isSuccessful) {
                    Toast.makeText(this@EditOwnerActivity, "Dueño actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditOwnerActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditOwnerActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnSave.isEnabled = true
                btnSave.text = "GUARDAR"
            }
        }
    }
}