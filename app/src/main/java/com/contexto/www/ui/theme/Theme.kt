package com.contexto.www.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MutedSage,
    onPrimary = DeepObsidian,
    secondary = IntelligentCopper,
    onSecondary = DeepObsidian,
    tertiary = SoftAmber,
    surface = DeepObsidian,
    onSurface = OffWhite,
    surfaceVariant = Charcoal,
    onSurfaceVariant = OffWhite,
    background = DeepObsidian,
    onBackground = OffWhite
)

@Composable
fun ContextoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography, // Assume standard or defined
        content = content
    )
}
