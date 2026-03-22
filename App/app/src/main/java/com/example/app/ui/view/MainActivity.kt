package com.example.app.ui.view

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.ui.modelview.CalculatorViewModel
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private val viewModel: CalculatorViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
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
        textView.setOnLongClickListener {
            viewModel.onCopyPressed()
            true
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.startObservingShake()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopObservingShake()
    }
}