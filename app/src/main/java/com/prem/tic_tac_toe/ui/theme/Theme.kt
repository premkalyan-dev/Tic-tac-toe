package com.prem.tic_tac_toe.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CoralOrange,
    onPrimary = Color.White,
    primaryContainer = CoralOrangeLight,
    onPrimaryContainer = Color.White,
    secondary = DeepIndigo,
    onSecondary = Color.White,
    secondaryContainer = DeepIndigoLight,
    onSecondaryContainer = Color.White,
    tertiary = XColor,
    background = WarmWhite,
    onBackground = Color(0xFF1C1B1F),
    surface = SoftCream,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = LightGray,
    onSurfaceVariant = Color(0xFF49454F),
    error = LossRed,
    onError = Color.White
)

@Composable
fun TicTacToeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}