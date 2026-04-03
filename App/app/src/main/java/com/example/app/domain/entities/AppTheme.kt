package com.example.app.domain.entities
data class AppTheme(
    val id: String,
    val name: String,
    val primaryColor: Int,
    val operatorColor: Int,
    val functionColor: Int,
    val numberColor: Int,
    val equalsColor: Int,
    val textColor: Int,
    val backgroundColor: Int = 0xFFFFFFFF.toInt()
)

object AppThemes {
    val allThemes = listOf(
        AppTheme(
            id = "blue_classic",
            name = "Классический синий",
            primaryColor = 0xFF1976D2.toInt(),
            operatorColor = 0xFFFE9F06.toInt(),
            functionColor = 0xFFA5A5A5.toInt(),
            numberColor = 0xFFF1F3F4.toInt(),
            equalsColor = 0xFF4CAF50.toInt(),
            textColor = 0xFF202124.toInt()
        ),
        AppTheme(
            id = "orange_vibrant",
            name = "Яркий оранжевый",
            primaryColor = 0xFFFF9800.toInt(),
            operatorColor = 0xFFF57C00.toInt(),
            functionColor = 0xFF78909C.toInt(),
            numberColor = 0xFFEEEEEE.toInt(),
            equalsColor = 0xFF66BB6A.toInt(),
            textColor = 0xFF212121.toInt()
        ),
        AppTheme(
            id = "green_nature",
            name = "Зелёный натуральный",
            primaryColor = 0xFF388E3C.toInt(),
            operatorColor = 0xFF2E7D32.toInt(),
            functionColor = 0xFF757575.toInt(),
            numberColor = 0xFFF1F8E9.toInt(),
            equalsColor = 0xFF66BB6A.toInt(),
            textColor = 0xFF212121.toInt()
        ),
        AppTheme(
            id = "purple_elegant",
            name = "Элегантный фиолетовый",
            primaryColor = 0xFF7B1FA2.toInt(),
            operatorColor = 0xFF8E24AA.toInt(),
            functionColor = 0xFF9E9E9E.toInt(),
            numberColor = 0xFFF3E5F5.toInt(),
            equalsColor = 0xFF66BB6A.toInt(),
            textColor = 0xFF212121.toInt()
        ),
        AppTheme(
            id = "dark_mode",
            name = "Тёмная тема",
            primaryColor = 0xFF212121.toInt(),
            operatorColor = 0xFFFFB300.toInt(),
            functionColor = 0xFF616161.toInt(),
            numberColor = 0xFF424242.toInt(),
            equalsColor = 0xFF66BB6A.toInt(),
            textColor = 0xFFFFFFFF.toInt(),
            backgroundColor = 0xFF121212.toInt()
        )
    )

    fun getById(id: String): AppTheme = allThemes.find { it.id == id }!!
}