package com.example.app.domain.entities

import kotlin.math.pow

class ExpressionEvaluator {
    fun calculate(expression: String): Result<Double> = runCatching {
        val tokens = tokenize(expression.replace(" ", ""))
        val postfixExpr = toPostfix(tokens)
        val stack = ArrayDeque<Double>()

        postfixExpr.forEach { t ->
            val number = t.toDoubleOrNull()
            if (number != null) {
                stack.addLast(number)
            } else {
                if (stack.size < 2) throw IllegalArgumentException("Invalid expression")

                val second = stack.removeLast()
                val first = stack.removeLast()
                stack.addLast(execute(t, first, second))
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
        stack.removeLast()
    }


    private fun tokenize(expression: String) : List<String> {
        val tokens = mutableListOf<String>()
        var number = ""
        var neg = false
        for (i in expression.indices) {
            val ch = expression[i]

            if (ch.isDigit() || ch == '.') {
                number += ch
            } else if (ch == '-' && (i == 0 || expression[i - 1] in "+-*/^(")) {
                neg = true
            } else {
                if (number.isNotEmpty()) {
                    if (neg) {
                        tokens.add("-$number")
                        neg = false
                    } else {
                        tokens.add(number)
                    }
                    number = ""
                }
                tokens.add(ch.toString())
            }
        }

        if (number.isNotEmpty()) tokens.add(number)
        return tokens
    }

    private fun toPostfix(tokens: List<String>) : List<String> {
        val operationPriority = mapOf(
            '(' to 0,
            '+' to 1,
            '-' to 1,
            '*' to 2,
            '/' to 2,
            '^' to 3,
            '~' to 4
        )

        val res = mutableListOf<String>()
        val operators = ArrayDeque<String>()

        tokens.forEach { t ->
            when {
                t.toDoubleOrNull() != null -> {
                    res.add(t)
                }

                t == "(" -> {
                    operators.addLast(t)
                }

                t == ")" -> {
                    while (operators.last() != "(") {
                        res.add(operators.removeLast())
                    }
                    operators.removeLast()
                }

                t == "~" -> {
                    res[res.lastIndex] = "-" + res.last()
                }

                else -> {
                    while (
                        operators.isNotEmpty() &&
                        operators.last() != "(" &&
                        operationPriority[operators.last()[0]]!! >= operationPriority[t[0]]!!
                    ) {
                        res.add(operators.removeLast())
                    }
                    operators.addLast(t)
                }
            }
        }

        while (operators.isNotEmpty()) {
            res.add(operators.removeLast())
        }

        return res
    }

    private fun execute(op: String, num1: Double, num2: Double): Double {
        return when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if (num2 == 0.0) {
                error("Divide by zero")
            } else {
                num1 / num2
            }
            "^" -> num1.pow(num2)
            else -> error("Unknown operator: $op")
        }
    }
}