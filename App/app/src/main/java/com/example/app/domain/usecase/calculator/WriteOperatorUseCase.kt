package com.example.app.domain.usecase.calculator

import com.example.app.domain.entities.ExpressionFormatter

class WriteOperatorUseCase(private val formatter: ExpressionFormatter) {
    operator fun invoke(expression: String, operator: String): Boolean {
        return formatter.canAddOperator(expression, operator)
    }
}