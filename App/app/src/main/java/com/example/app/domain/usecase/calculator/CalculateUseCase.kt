package com.example.app.domain.usecase.calculator

import com.example.app.domain.entities.ExpressionEvaluator

class CalculateUseCase(private val evaluator: ExpressionEvaluator) {
    operator fun invoke(expression: String): Result<Double> {
        return evaluator.calculate(expression)
    }
}