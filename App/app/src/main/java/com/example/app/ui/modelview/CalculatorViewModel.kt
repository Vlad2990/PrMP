package com.example.app.ui.modelview

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.domain.entities.ExpressionEvaluator
import com.example.app.domain.entities.ExpressionFormatter
import com.example.app.domain.usecase.AddBracketsUseCase
import com.example.app.domain.usecase.CalculateUseCase
import com.example.app.domain.usecase.ClearOnShakeUseCase
import com.example.app.domain.usecase.CopyToClipboardUseCase
import com.example.app.domain.usecase.ToggleSignUseCase
import com.example.app.domain.usecase.VibrateUseCase
import com.example.app.domain.usecase.WriteOperatorUseCase

class CalculatorViewModel(
    private val calculateUseCase: CalculateUseCase,
    private val toggleSignUseCase: ToggleSignUseCase,
    private val addBracketsUseCase: AddBracketsUseCase,
    private val writeOperatorUseCase: WriteOperatorUseCase,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val vibrateUseCase: VibrateUseCase,
    private val clearOnShakeUseCase: ClearOnShakeUseCase
) : ViewModel() {
    private val _expression = MutableLiveData("")
    val expression: LiveData<String> get() = _expression
    private val operators = setOf("+", "-", "*", "/", "^")

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

}