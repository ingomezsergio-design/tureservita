package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = NightBg,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldLight,
    secondary = SkyAccent,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    onTertiary = NightBg,
    background = NightBg,
    onBackground = TextPrimary,
    surface = NightSurface,
    onSurface = TextPrimary,
    surfaceVariant = NightCard,
    onSurfaceVariant = TextMuted,
    outline = GlassBorder,
    error = RoseAccent,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
