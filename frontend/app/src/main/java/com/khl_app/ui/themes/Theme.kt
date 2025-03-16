package com.khl_app.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography as Typography3
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF424769),
    secondary = Color(0xFF7077A1),
    background = Color(0xFF2D3250),
    surface = Color(0xFFF6B17A),
    onPrimary = Color(0xFF424769),
    onSecondary = Color(0xFF7077A1),
    onBackground = Color(0xFF2D3250),
    onSurface = Color(0xFFF6B17A)
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFFE5D9F2),
    secondary = Color(0xFFA294F9),
    background = Color(0xFFF5EFFF),
    surface = Color(0xFFCDC1FF),
    onPrimary = Color(0xFFE5D9F2),
    onSecondary = Color(0xFFA294F9),
    onBackground = Color(0xFFF5EFFF),
    onSurface = Color(0xFFCDC1FF)
)

@Composable
fun KhlAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = DarkColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = Typography3(),
        shapes = Shapes(),
        content = content
    )
}