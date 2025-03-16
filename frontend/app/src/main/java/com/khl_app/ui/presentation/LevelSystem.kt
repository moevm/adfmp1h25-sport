package com.khl_app.domain.models

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a user level in the application
 */
data class Level(
    val number: Int,
    val minPoints: Int,
    val label: String,
    val color: Color,
    val borderColor: Color
)

/**
 * Singleton object that manages user levels based on points
 */
object LevelSystem {
    // List of predefined levels
    val levels = listOf(
        Level(
            number = 1,
            minPoints = 0,
            label = "Новичок",
            color = Color.White,
            borderColor = Color(0xFF999999) // Серый
        ),
        Level(
            number = 2,
            minPoints = 100,
            label = "Продвинутый",
            color = Color(0xFF00FF00), // Зеленый
            borderColor = Color(0xFF00FF00) // Зеленый
        ),
        Level(
            number = 3,
            minPoints = 500,
            label = "Мастер",
            color = Color(0xFFFFAA00), // Оранжевый
            borderColor = Color(0xFFFFFF00) // Желтый
        ),
        Level(
            number = 4,
            minPoints = 1000,
            label = "Легенда",
            color = Color(0xFFFFFF00), // Желтый
            borderColor = Color(0xFFFF5555) // Красный
        ),
    )

    /**
     * Get level based on points
     */
    fun getLevelForPoints(points: Int): Level {
        return levels.findLast { level -> points >= level.minPoints } ?: levels.first()
    }

    fun getPointsColor(points: Int): Color {
        return getLevelForPoints(points).color
    }

    fun getAvatarBorderColor(points: Int): Color {
        return getLevelForPoints(points).borderColor
    }

    /**
     * Get level label (name) based on points
     */
    fun getLevelLabel(points: Int): String {
        return getLevelForPoints(points).label
    }

    /**
     * Get level number based on points
     */
    fun getLevelNumber(points: Int): Int {
        return getLevelForPoints(points).number
    }
}