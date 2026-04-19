package com.example.app.domain.usecase.calculator

import com.example.app.domain.entities.ExpressionFormatter

class AddBracketsUseCase(private val formatter: ExpressionFormatter) {
    operator fun invoke(expression: String): String {
        return formatter.addBracket(expression)
    }
}