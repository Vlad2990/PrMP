package com.example.app.ui.view

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.bundle.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.domain.usecase.auth.LoginUseCase
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue

class LoginActivity : AppCompatActivity() {

    private val loginUseCase: LoginUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etLoginEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Ошибка входа", Toast.LENGTH_SHORT).show()
            }
        }
        val isResetMode = intent.getBooleanExtra("RESET_PIN", false)
        if (isResetMode) {
            tvGoToRegister.visibility = View.GONE
        }
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val success = loginUseCase(email, password)
                if (success) {
                    val isReset = intent.getBooleanExtra("RESET_PIN", false)

                    if (isReset) {
                        val intentPassKey = Intent(this@LoginActivity, PassKeyActivity::class.java)
                        intentPassKey.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentPassKey.putExtra("RESET_PIN", true)
                        startActivity(intentPassKey)
                    } else {
                        val intentMain = Intent(this@LoginActivity, MainActivity::class.java)
                        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intentMain)
                    }
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Ошибка входа: проверьте данные", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Произошла ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}