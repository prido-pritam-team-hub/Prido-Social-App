package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PridoDarkColorScheme = darkColorScheme(
    primary = PridoCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0E3A4B),
    onPrimaryContainer = PridoCyanLight,
    secondary = PridoIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF282B5E),
    onSecondaryContainer = PridoIndigoLight,
    tertiary = PridoViolet,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3B1E63),
    onTertiaryContainer = PridoVioletLight,
    background = PridoBackground,
    onBackground = PridoTextPrimary,
    surface = PridoSurface,
    onSurface = PridoTextPrimary,
    surfaceVariant = PridoSurfaceVariant,
    onSurfaceVariant = PridoTextSecondary,
    outline = PridoBorder,
    outlineVariant = PridoBorderSubtle,
    error = PridoError,
    onError = Color.White
)

@Composable
fun PridoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PridoDarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for template compatibility if referenced anywhere
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PridoTheme(content = content)
}
