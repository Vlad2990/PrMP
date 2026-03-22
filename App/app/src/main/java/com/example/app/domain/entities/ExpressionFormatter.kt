package com.example.app.domain.entities

class ExpressionFormatter {
    private val lastNumberRegex = Regex("""-?\d+(\.\d+)?$""")
    private val operators = setOf('+', '-', '*', '/', '^')
    fun toggleSign(expression: String): String {
        if (expression.isEmpty()) return ""

        val match = lastNumberRegex.find(expression) ?: return expression

        val lastNumber = match.value
        val startIndex = match.range.first
        val prefix = expression.substring(0, startIndex)

        return if (lastNumber.startsWith("-")) {
            if (prefix.endsWith("(")) {
                prefix.dropLast(1) + lastNumber.drop(1)
            } else {
                prefix + lastNumber.drop(1)
            }
        } else {
            if (prefix.endsWith("(")) {
                "$prefix-$lastNumber"
            } else {
                "$prefix(-$lastNumber"
            }
        }
    }

    fun addBracket(expression: String): String {
        if (expression.isEmpty()) return "("

        val lastChar = expression.last()
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }

        return when {
            openCount > closeCount && (lastChar.isDigit() || lastChar == ')') -> {
                "$expression)"
            }

            lastChar in "+-*/^(" -> {
                "$expression("
            }

            else -> if (lastChar.isDigit() || lastChar == ')') "$expression*(" else "$expression("
        }
    }


    fun canAddOperator(expression: String, newOperator: String): Boolean {
        if (expression.isEmpty()) {
            return false
        }

        val lastChar = expression.last()

        if (lastChar in operators) {
            return false
        }

        if (lastChar == '(' && newOperator != "-") {
            return false
        }

        return true
    }
}