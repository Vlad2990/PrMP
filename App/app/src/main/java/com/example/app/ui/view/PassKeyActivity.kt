package com.example.app.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.domain.usecase.auth.PassKeySetCheckUseCase
import com.example.app.domain.usecase.auth.SetPassKeyUseCase
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PassKeyActivity : AppCompatActivity() {

    private val setPassKeyUseCase: SetPassKeyUseCase by inject()
    private val passKeySetCheckUseCase: PassKeySetCheckUseCase by inject()

    private var isConfirmStep = false
    private var firstPin = ""

    private lateinit var tvTitle: TextView
    private lateinit var tvError: TextView
    private lateinit var btnNext: Button
    private lateinit var etHiddenPin: TextInputEditText
    private val pinViews = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passkey)

        initViews()
        setupPinInput()
        checkIfPassKeyAlreadySet()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        tvError = findViewById(R.id.tvError)
        btnNext = findViewById(R.id.btnNext)
        etHiddenPin = findViewById(R.id.etHiddenPin)

        pinViews.apply {
            add(findViewById<View>(R.id.pin1).findViewById(R.id.tvDigit))
            add(findViewById<View>(R.id.pin2).findViewById(R.id.tvDigit))
            add(findViewById<View>(R.id.pin3).findViewById(R.id.tvDigit))
            add(findViewById<View>(R.id.pin4).findViewById(R.id.tvDigit))
            add(findViewById<View>(R.id.pin5).findViewById(R.id.tvDigit))
            add(findViewById<View>(R.id.pin6).findViewById(R.id.tvDigit))
        }

        findViewById<LinearLayout>(R.id.pinContainer).setOnClickListener {
            forceFocus()
        }

        btnNext.setOnClickListener { handleNextClick() }
    }

    private fun setupPinInput() {
        forceFocus()

        etHiddenPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pin = s.toString()
                updatePinUI(pin)

                if (pin.length == 6) {
                    etHiddenPin.post { handleNextClick() }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun forceFocus() {
        etHiddenPin.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etHiddenPin, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun updatePinUI(pin: String) {
        for (i in pinViews.indices) {
            pinViews[i].text = if (i < pin.length) pin[i].toString() else ""
        }
    }

    private fun handleNextClick() {
        val currentPin = etHiddenPin.text.toString().trim()

        if (currentPin.length != 6) {
            showError("Pass Key должен состоять из 6 цифр")
            return
        }

        if (!isConfirmStep) {
            firstPin = currentPin
            isConfirmStep = true
            tvTitle.text = "Повторите Pass Key"
            resetInputState()
        } else {
            if (currentPin == firstPin) {
                setupPassKey(currentPin)
            } else {
                showError("Pass Key не совпадает. Попробуйте снова.")
                resetInputState()
            }
        }
    }

    private fun resetInputState() {
        etHiddenPin.post {
            etHiddenPin.setText("")
            clearPinUI()
            tvError.visibility = View.INVISIBLE
        }
    }

    private fun setupPassKey(pin: String) {
        lifecycleScope.launch {
            val success = setPassKeyUseCase(pin)
            if (success) {
                Toast.makeText(this@PassKeyActivity, "Pass Key успешно установлен!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@PassKeyActivity, MainActivity::class.java))
                finish()
            } else {
                showError("Ошибка при сохранении Pass Key")
            }
        }
    }

    private fun clearPinUI() {
        pinViews.forEach { it.text = "" }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun checkIfPassKeyAlreadySet() {
        if (intent.getBooleanExtra("RESET_PIN", false)) return
        lifecycleScope.launch {
            val isSet = passKeySetCheckUseCase()
            if (isSet) {
                startActivity(Intent(this@PassKeyActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}