package com.example.veteica.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.veteica.R
import com.example.veteica.activities.panel.HomeActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etCedula = findViewById(R.id.etCedula)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etTelefono = findViewById(R.id.etTelefono)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        tvLogin = findViewById(R.id.tvLogin)

        // Botón Registrarse
        btnRegistrarse.setOnClickListener {
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val cedula = etCedula.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val telefono = etTelefono.text.toString()

            when {
                nombre.isEmpty() -> Toast.makeText(this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show()
                apellido.isEmpty() -> Toast.makeText(this, "Ingresa tu apellido", Toast.LENGTH_SHORT).show()
                cedula.isEmpty() -> Toast.makeText(this, "Ingresa tu cédula profesional", Toast.LENGTH_SHORT).show()
                email.isEmpty() -> Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                password.isEmpty() -> Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                telefono.isEmpty() -> Toast.makeText(this, "Ingresa tu teléfono", Toast.LENGTH_SHORT).show()
                else -> {
                    Toast.makeText(this, "Registro exitoso. Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
        }

        // Botón Cancelar
        btnCancelar.setOnClickListener {
            finish()
        }

        // Link Iniciar sesión
        tvLogin.setOnClickListener {
            finish()
        }
    }
}