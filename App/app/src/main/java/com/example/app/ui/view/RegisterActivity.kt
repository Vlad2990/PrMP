package com.example.app.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.domain.usecase.auth.PassKeySetCheckUseCase
import com.example.app.domain.usecase.auth.RegisterUseCase
import com.example.app.domain.usecase.auth.UserRegisterCheckUseCase
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {
    private val registerUseCase: RegisterUseCase by inject()
    private val userRegisterCheckUseCase: UserRegisterCheckUseCase by inject()
    private val isPassKeySetUseCase: PassKeySetCheckUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initViews()
        checkUserState()
    }

    private fun initViews() {
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                registerUser(email, password)
            }
        }
        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkUserState() {
        lifecycleScope.launch {
            val isUserRegistered = userRegisterCheckUseCase()

            if (isUserRegistered) {
                val isPassKeySet = isPassKeySetUseCase()
                if (isPassKeySet) {
                    goToMain()
                } else {
                    goToSetupPassKey()
                }
            }
        }
    }

    private suspend fun registerUser(email: String, password: String) {
        val res = registerUseCase(email, password)
        if (res) {
            Toast.makeText(this, "Аккаунт создан успешно!", Toast.LENGTH_SHORT).show()
            goToSetupPassKey()
        } else {
            Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToSetupPassKey() {
        startActivity(Intent(this, PassKeyActivity::class.java))
        finish()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}