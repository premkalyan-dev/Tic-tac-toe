package com.prem.tic_tac_toe.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.prem.tic_tac_toe.data.SettingsManager

// ─── Default Theme ───
private val DefaultColorScheme = lightColorScheme(
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

// ─── Ocean Theme ───
private val OceanColorScheme = lightColorScheme(
    primary = Color(0xFF0288D1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4FC3F7),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4DB6AC),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF0097A7),
    background = Color(0xFFF0F8FF),
    onBackground = Color(0xFF1A2733),
    surface = Color(0xFFE8F4FD),
    onSurface = Color(0xFF1A2733),
    surfaceVariant = Color(0xFFDCEEF7),
    onSurfaceVariant = Color(0xFF3D5A6E),
    error = LossRed,
    onError = Color.White
)

// ─── Forest Theme ───
private val ForestColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF66BB6A),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF5D4037),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF8D6E63),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF558B2F),
    background = Color(0xFFF1F8E9),
    onBackground = Color(0xFF1B2E1B),
    surface = Color(0xFFE8F5E9),
    onSurface = Color(0xFF1B2E1B),
    surfaceVariant = Color(0xFFDCEDC8),
    onSurfaceVariant = Color(0xFF4A5E3C),
    error = LossRed,
    onError = Color.White
)

// ─── Sunset Theme ───
private val SunsetColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFF8A65),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFC62828),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEF5350),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFF6F00),
    background = Color(0xFFFFF8E1),
    onBackground = Color(0xFF2E1A0E),
    surface = Color(0xFFFFF3E0),
    onSurface = Color(0xFF2E1A0E),
    surfaceVariant = Color(0xFFFFE0B2),
    onSurfaceVariant = Color(0xFF5D4037),
    error = LossRed,
    onError = Color.White
)

// ─── Dark Theme ───
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A65),
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFFBF360C),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF00695C),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFCE93D8),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = Color(0xFFEF5350),
    onError = Color.White
)

fun getColorScheme(themeName: String): ColorScheme {
    return when (themeName) {
        SettingsManager.THEME_OCEAN -> OceanColorScheme
        SettingsManager.THEME_FOREST -> ForestColorScheme
        SettingsManager.THEME_SUNSET -> SunsetColorScheme
        SettingsManager.THEME_DARK -> DarkColorScheme
        else -> DefaultColorScheme
    }
}

@Composable
fun TicTacToeTheme(
    themeName: String = SettingsManager.THEME_DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(themeName)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}