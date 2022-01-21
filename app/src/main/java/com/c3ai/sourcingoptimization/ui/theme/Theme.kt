package com.c3ai.sourcingoptimization.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Primary,
    secondary = Secondary,
    background = BackgroundPrimary,
    onBackground = Color.White,
    surface = LightBlue,
    onPrimary = OnPrimary,
    onSurface = BackgroundPrimary,
)

@Composable
fun C3AppTheme(darkTheme: Boolean = true, content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}