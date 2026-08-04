package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = SurfaceDark,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentOrange,
    onSecondary = TextPrimary,
    background = SurfaceDark,
    onBackground = TextPrimary,
    surface = CharcoalDark,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = TextPrimary,
    primaryContainer = GoldPrimary,
    onPrimaryContainer = SurfaceDark,
    secondary = AccentOrange,
    onSecondary = TextPrimary,
    background = Color(0xFFF5F5F5),
    onBackground = CharcoalDark,
    surface = Color.White,
    onSurface = CharcoalDark,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = CharcoalLight,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
