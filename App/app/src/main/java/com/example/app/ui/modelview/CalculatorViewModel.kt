package com.example.app.ui.modelview

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.entities.AppTheme
import com.example.app.domain.entities.AppThemes
import com.example.app.domain.entities.ExpressionEvaluator
import com.example.app.domain.entities.ExpressionFormatter
import com.example.app.domain.entities.HistoryItem
import com.example.app.domain.usecase.auth.VerifyPassKeyUseCase
import com.example.app.domain.usecase.calculator.AddBracketsUseCase
import com.example.app.domain.usecase.calculator.CalculateUseCase
import com.example.app.domain.usecase.systemapi.ClearOnShakeUseCase
import com.example.app.domain.usecase.systemapi.CopyToClipboardUseCase
import com.example.app.domain.usecase.firestore.GetHistoryUseCase
import com.example.app.domain.usecase.firestore.GetThemeUseCase
import com.example.app.domain.usecase.firestore.SaveToHistoryUseCase
import com.example.app.domain.usecase.firestore.SetThemeUseCase
import com.example.app.domain.usecase.calculator.ToggleSignUseCase
import com.example.app.domain.usecase.systemapi.VibrateUseCase
import com.example.app.domain.usecase.calculator.WriteOperatorUseCase
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val calculateUseCase: CalculateUseCase,
    private val toggleSignUseCase: ToggleSignUseCase,
    private val addBracketsUseCase: AddBracketsUseCase,
    private val writeOperatorUseCase: WriteOperatorUseCase,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val vibrateUseCase: VibrateUseCase,
    private val clearOnShakeUseCase: ClearOnShakeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val saveToHistoryUseCase: SaveToHistoryUseCase,
    private val verifyPassKeyUseCase: VerifyPassKeyUseCase
) : ViewModel() {
    private val _expression = MutableLiveData("")
    val expression: LiveData<String> get() = _expression
    private val _currentTheme = MutableLiveData<AppTheme>(AppThemes.allThemes.first())
    val currentTheme: LiveData<AppTheme> = _currentTheme
    private val operators = setOf("+", "-", "*", "/", "^")
    private val _history = MutableLiveData<List<HistoryItem>>()
    val history: LiveData<List<HistoryItem>> = _history
    var authorized = false
    private val _pinVerificationResult = MutableLiveData<Boolean?>(null)
    val pinVerificationResult: LiveData<Boolean?> = _pinVerificationResult
    fun onKeyPressed(key: String) {
        vibrateUseCase()
        if (!_expression.value.isNullOrEmpty() &&
            (_expression.value == "Error" || _expression.value == "Infinity")) _expression.value = ""
        val currentExpr = _expression.value ?: ""
        if (currentExpr.isNotEmpty() &&
            currentExpr.last() == '.' && !key.isDigitsOnly())
            _expression.value = currentExpr + 0
        when (key) {
            "C" -> _expression.value = ""
            "=" -> {
                if (currentExpr.isEmpty() || currentExpr == "Error") return

                val hasOperator = operators.any { currentExpr.contains(it) }
                val isLastOperator = currentExpr.last().toString() in operators

                if (!hasOperator || isLastOperator) return

                val result = calculateUseCase(currentExpr)

                _expression.value = result.getOrNull()?.let { res ->
                    if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
                } ?: "Error"
                if (_expression.value != "Error") {
                    saveAction(currentExpr, _expression.value.toString())
                }
            }
            "+/-" -> {
                if (currentExpr.isEmpty()) return
                _expression.value = toggleSignUseCase(currentExpr)
            }
            "()" -> {
                _expression.value = addBracketsUseCase(currentExpr)
            }
            else -> {
                if (operators.contains(key) && !writeOperatorUseCase(currentExpr, key)) {
                    return
                }
                _expression.value = (currentExpr) + key
            }
        }
    }
    fun onCopyPressed() {
        copyToClipboardUseCase(_expression.value ?: "")
    }
    fun startObservingShake() {
        clearOnShakeUseCase.execute {
            _expression.value = ""
        }
    }
    fun stopObservingShake() {
        clearOnShakeUseCase.cleanup()
    }
    fun onHistoryItemClicked(item: HistoryItem) {
        _expression.value = item.expression
    }
    fun loadTheme() {
        viewModelScope.launch {
            val savedId = getThemeUseCase()
            val theme = AppThemes.getById(savedId ?: "blue_classic")
            _currentTheme.value = theme
        }
    }
    fun selectTheme(theme: AppTheme) {
        _currentTheme.value = theme
        viewModelScope.launch {
            setThemeUseCase(theme.id)
        }
    }
    fun saveAction(expr: String, res: String) {
        viewModelScope.launch {
            val item = HistoryItem(expr, res)
            saveToHistoryUseCase(item)
            getHistory()
        }
    }
    fun getHistory() {
        viewModelScope.launch {
            _history.value = getHistoryUseCase()
        }
    }

    fun verifyPin(pin: String) {
        viewModelScope.launch {
            val isValid = verifyPassKeyUseCase(pin)
            authorized = isValid
            _pinVerificationResult.value = isValid
        }
    }

    fun resetPinVerification() {
        _pinVerificationResult.value = null
    }
}