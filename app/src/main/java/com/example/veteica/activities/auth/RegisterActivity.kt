package com.example.veteica.activities.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.veteica.R
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCedula: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnCancelar: Button
    private lateinit var btnRegistrarse: Button
    private lateinit var tvLogin: TextView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)

        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCedula = findViewById(R.id.etCedula)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etTelefono = findViewById(R.id.etTelefono)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        tvLogin = findViewById(R.id.tvLogin)

        btnRegistrarse.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()

            when {
                nombre.isEmpty() -> { Toast.makeText(this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                apellido.isEmpty() -> { Toast.makeText(this, "Ingresa tu apellido", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                cedula.isEmpty() -> { Toast.makeText(this, "Ingresa tu cédula profesional", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                email.isEmpty() -> { Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                password.isEmpty() -> { Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                telefono.isEmpty() -> { Toast.makeText(this, "Ingresa tu teléfono", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            }

            btnRegistrarse.isEnabled = false
            btnRegistrarse.text = "Registrando..."

            lifecycleScope.launch {
                try {
                    val body = mapOf(
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "cedula" to cedula,
                        "email" to email,
                        "password" to password,
                        "telefono" to telefono
                    )
                    val response = RetrofitClient.instance.register(body)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val success = responseBody?.get("success") as? Boolean

                        if (success == true) {
                            val data = responseBody["data"] as? Map<*, *>
                            val token = data?.get("token") as? String

                            if (token != null) {
                                prefs.edit()
                                    .putString("token", token)
                                    .putString("user_name", nombre)
                                    .apply()
                                Toast.makeText(this@RegisterActivity, "Registro exitoso. Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                                finish()
                            }
                        } else {
                            val error = responseBody?.get("error") as? Map<*, *>
                            val message = error?.get("message") as? String ?: "Error al registrarse"
                            Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error al registrarse", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnRegistrarse.isEnabled = true
                    btnRegistrarse.text = "REGISTRARSE"
                }
            }
        }

        btnCancelar.setOnClickListener { finish() }
        tvLogin.setOnClickListener { finish() }
    }
}