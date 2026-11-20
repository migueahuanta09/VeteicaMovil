package com.example.veteica.activities.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.veteica.activities.panel.HomeActivity
import com.example.veteica.databinding.ActivityLoginBinding
import com.example.veteica.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("veteica_prefs", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val cedula = binding.etCedula.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || cedula.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Entrando..."

            lifecycleScope.launch {
                try {
                    val body = mapOf(
                        "email" to email,
                        "password" to password,
                        "cedula" to cedula
                    )
                    val response = RetrofitClient.instance.login(body)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val success = responseBody?.get("success") as? Boolean

                        if (success == true) {
                            val data = responseBody["data"] as? Map<*, *>
                            val token = data?.get("token") as? String
                            val user = data?.get("user") as? Map<*, *>

                            val userName = user?.get("nombre") as? String ?: ""
                            val apellido = user?.get("apellido") as? String ?: ""
                            val userCedula = user?.get("cedula") as? String ?: ""
                            val userEmail = user?.get("email") as? String ?: ""
                            val telefono = user?.get("telefono") as? String ?: ""

                            if (token != null) {
                                prefs.edit()
                                    .putString("token", token)
                                    .putString("user_name", userName)
                                    .putString("user_apellido", apellido)
                                    .putString("user_cedula", userCedula)
                                    .putString("user_email", userEmail)
                                    .putString("user_phone", telefono)
                                    .apply()

                                Toast.makeText(this@LoginActivity, "Bienvenido a VETEICA", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                finish()
                            }
                        } else {
                            val error = responseBody?.get("error") as? Map<*, *>
                            val message = error?.get("message") as? String ?: "Error al iniciar sesión"
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "ENTRAR"
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}