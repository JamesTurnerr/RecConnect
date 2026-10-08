package com.example.recconnect.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Light only: the logo PNG has a white background, so a dark theme would show a white box.
// Dynamic (wallpaper) colour is deliberately NOT used, otherwise Android 12+ ignores the brand red.
private val RecConnectColors = lightColorScheme(
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
fun RecConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RecConnectColors,
        typography = Typography,   // from your existing Type.kt
        shapes = RecConnectShapes,
        content = content
    )
}