package com.example.app.domain.usecase

import com.example.app.domain.entities.ExpressionFormatter

class ToggleSignUseCase(private val formatter: ExpressionFormatter) {
    operator fun invoke(expression: String): String {
        return formatter.toggleSign(expression)
    }
}