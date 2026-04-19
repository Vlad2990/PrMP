package com.example.app.ui.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat.applyTheme
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.gridlayout.widget.GridLayout
import com.example.app.R
import com.example.app.ui.modelview.CalculatorViewModel
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.domain.entities.AppTheme
import com.example.app.domain.entities.AppThemes
import com.example.app.ui.history.HistoryBottomSheet
import com.example.app.ui.notifications.NotificationHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private val viewModel: CalculatorViewModel by viewModel()
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var historySpinner: Spinner
    private val historyAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        notificationHelper = NotificationHelper(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textView = findViewById<TextView>(R.id.expr)
        val keypad = findViewById<androidx.gridlayout.widget.GridLayout>(R.id.keypad)
        viewModel.expression.observe(this) { newExpr ->
            textView.text = newExpr
        }
        for (i in 0 until keypad.childCount) {
            val child = keypad.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener {
                    viewModel.onKeyPressed(child.text.toString())
                }
            }
        }

        val prefButton = findViewById<ImageButton>(R.id.prefs)

        prefButton.setOnClickListener {
            showThemeSelectorDialog()
        }

        val historyButton = findViewById<ImageButton>(R.id.hist)

        historyButton.setOnClickListener {
            if (viewModel.authorized) {
                showHistoryBottomSheet()
            } else {
                authenticateForHistory()
            }
        }

        textView.setOnLongClickListener {
            viewModel.onCopyPressed()
            true
        }
        viewModel.currentTheme.observe(this) { theme ->
            applyTheme(theme)
        }

        viewModel.loadTheme()
        viewModel.getHistory()
        viewModel.pinVerificationResult.observe(this) { isValid ->
            when (isValid) {
                true -> {
                    showHistoryBottomSheet()
                    viewModel.resetPinVerification()
                }
                false -> {
                    Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show()
                    viewModel.resetPinVerification()
                }
                null -> {}
            }
        }
    }
    private fun showCustomPinDialog() {
        val themedContext = androidx.appcompat.view.ContextThemeWrapper(
            this,
            com.google.android.material.R.style.Theme_MaterialComponents_DayNight_DarkActionBar
        )

        val dialogView = layoutInflater.inflate(R.layout.pin_dialog, null)
        val input = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.customPinEditText)
        val forgotPasswordBtn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnForgotPassword)
        val dialog = MaterialAlertDialogBuilder(themedContext)
            .setTitle("Доступ по PIN-коду")
            .setView(dialogView)
            .setPositiveButton("Войти") { _, _ ->
                val pin = input.text.toString()
                viewModel.verifyPin(pin)
                }
            .setNegativeButton("Отмена", null)
            .create()
        forgotPasswordBtn.setOnClickListener {
            dialog.dismiss()
            handlePasswordReset()
        }
        dialog.show()
    }
    private fun handlePasswordReset() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.putExtra("RESET_PIN", true)
        startActivity(intent)
    }
    private fun authenticateForHistory() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.authorized = true
                    showHistoryBottomSheet()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showCustomPinDialog()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Вход в историю")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Использовать PIN")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
    private fun showHistoryBottomSheet() {
        val bottomSheet = HistoryBottomSheet()
        bottomSheet.show(supportFragmentManager, "HistoryBottomSheetTag")
    }

    override fun onResume() {
        super.onResume()
        viewModel.startObservingShake()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopObservingShake()
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing && !isChangingConfigurations) {
            notificationHelper.showReturnNotification()
        }
    }
    private fun showThemeSelectorDialog() {
        val themes = AppThemes.allThemes
        val names = themes.map { it.name }.toTypedArray()

        val currentThemeId = viewModel.currentTheme.value?.id
        val checkedItem = themes.indexOfFirst { it.id == currentThemeId }.coerceAtLeast(0)

        val dialog = MaterialAlertDialogBuilder(this, R.style.Theme_App)
            .setTitle("Выбор темы")
            .setSingleChoiceItems(names, checkedItem) { dialog, which ->
                val selectedTheme = themes[which]
                viewModel.selectTheme(selectedTheme)
                dialog.dismiss()
            }
            .show()
        dialog.window?.let { window ->
            val params = window.attributes

            params.width = (resources.displayMetrics.widthPixels * 0.90).toInt()

            params.height = WindowManager.LayoutParams.WRAP_CONTENT

            window.attributes = params
        }
    }
    private fun applyTheme(theme: AppTheme) {
        window.statusBarColor = theme.primaryColor
        val isLightStatusBar = ColorUtils.calculateLuminance(theme.primaryColor) > 0.5
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLightStatusBar

        val keypad = findViewById<GridLayout>(R.id.keypad)
        for (i in 0 until keypad.childCount) {
            val child = keypad.getChildAt(i)
            if (child is Button) {
                when (child.text.toString()) {
                    "C", "()", "^", "+/-" -> {
                        child.backgroundTintList = ColorStateList.valueOf(theme.functionColor)
                        child.setTextColor(theme.textColor)
                    }
                    "+", "-", "*", "/", "^" -> {
                        child.backgroundTintList = ColorStateList.valueOf(theme.operatorColor)
                        child.setTextColor(Color.WHITE)
                    }
                    "=" -> {
                        child.backgroundTintList = ColorStateList.valueOf(theme.equalsColor)
                        child.setTextColor(Color.WHITE)
                    }
                    else -> {
                        child.backgroundTintList = ColorStateList.valueOf(theme.numberColor)
                        child.setTextColor(theme.textColor)
                    }
                }
            }
        }
        findViewById<View>(R.id.main).setBackgroundColor(theme.backgroundColor ?: Color.WHITE)
        findViewById<TextView>(R.id.expr).setTextColor(theme.textColor)
    }
}