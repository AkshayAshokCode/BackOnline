package com.akshayAshokCode.backonline.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * BackOnline palette: calm and reassuring. Green means online, warm amber means waiting,
 * a muted red means disconnected — never an alarming red as the main colour.
 */
object BrandColors {
    val Green      = Color(0xFF1E9E6A)
    val GreenLight = Color(0xFF3CC78D)
    val Amber      = Color(0xFFE0A030)
    val Red        = Color(0xFFD2584C)

    val LightBackground = Color(0xFFF6F7F5)
    val LightSurface    = Color(0xFFFFFFFF)
    val LightText       = Color(0xFF17201C)
    val LightTextMuted  = Color(0xFF5E6A64)
    val LightOutline    = Color(0xFFDDE3DF)

    val DarkBackground = Color(0xFF0F1513)
    val DarkSurface    = Color(0xFF18201D)
    val DarkText       = Color(0xFFE6ECE9)
    val DarkTextMuted  = Color(0xFF9AA7A1)
    val DarkOutline    = Color(0xFF2A3430)
}

private val LightScheme = lightColorScheme(
    primary = BrandColors.Green,
    onPrimary = Color.White,
    background = BrandColors.LightBackground,
    onBackground = BrandColors.LightText,
    surface = BrandColors.LightSurface,
    onSurface = BrandColors.LightText,
    onSurfaceVariant = BrandColors.LightTextMuted,
    outline = BrandColors.LightOutline,
    outlineVariant = BrandColors.LightOutline,
    error = BrandColors.Red
)

private val DarkScheme = darkColorScheme(
    primary = BrandColors.GreenLight,
    onPrimary = BrandColors.DarkBackground,
    background = BrandColors.DarkBackground,
    onBackground = BrandColors.DarkText,
    surface = BrandColors.DarkSurface,
    onSurface = BrandColors.DarkText,
    onSurfaceVariant = BrandColors.DarkTextMuted,
    outline = BrandColors.DarkOutline,
    outlineVariant = BrandColors.DarkOutline,
    error = BrandColors.Red
)

@Composable
fun BackOnlineTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkScheme else LightScheme, content = content)
}
