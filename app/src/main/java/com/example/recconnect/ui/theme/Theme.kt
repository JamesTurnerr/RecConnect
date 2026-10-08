package com.example.recconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = BrandRedLight,
    onPrimary = Color.Black,
    primaryContainer = BrandRedDark,
    onPrimaryContainer = BrandRedLight,
    secondary = Charcoal,
    onSecondary = Color.White,
    secondaryContainer = Warm200,
    onSecondaryContainer = Charcoal,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Charcoal,
    onSurfaceVariant = WarmGrey,
    outline = WarmOutline,
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF2B8B5)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    primaryContainer = BrandRedLight,
    onPrimaryContainer = BrandRedDark,
    secondary = Charcoal,
    onSecondary = Color.White,
    secondaryContainer = Warm200,
    onSecondaryContainer = Charcoal,
    background = Warm100,
    onBackground = Charcoal,
    surface = Warm100,
    onSurface = Charcoal,
    surfaceVariant = Warm200,
    onSurfaceVariant = WarmGrey,
    outline = WarmOutline,
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFFDE7E4),
    onErrorContainer = Color(0xFF601410)
)

private val RecConnectShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun RecConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,   // from your existing Type.kt
        shapes = RecConnectShapes,
        content = content
    )
}
