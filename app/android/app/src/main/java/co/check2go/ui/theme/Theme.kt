package co.check2go.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

data class Check2GoTokens(
    val card: Color,
    val cardSecondary: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val disabled: Color
)

val LocalCheck2GoTokens = staticCompositionLocalOf {
    Check2GoTokens(Color.White, Color.White, Color(0xFFE5E7EB), Color(0xFF2E7D32), Color(0xFFB26A00), Color(0xFF8D919A))
}

private val LightColors = lightColorScheme(
    primary = C2GPrimary,
    onPrimary = Color.White,
    background = C2GLightBackground,
    onBackground = C2GLightTextPrimary,
    surface = Color.White,
    onSurface = C2GLightTextPrimary,
    onSurfaceVariant = C2GTextSecondary,
    error = C2GError
)

private val DarkColors = darkColorScheme(
    primary = C2GPrimary,
    onPrimary = Color.White,
    background = C2GDarkBackground,
    onBackground = C2GDarkTextPrimary,
    surface = Color(0xFF253C55),
    onSurface = C2GDarkTextPrimary,
    surfaceVariant = C2GDarkDisabledSurface,
    onSurfaceVariant = C2GTextSecondary,
    error = C2GError
)

/**
 * Check2GO product theme.
 *
 * Only design roles confirmed by the current Figma UI kit are overridden here.
 * Do not promote other Material defaults to product tokens without design approval.
 * Dynamic color is intentionally not used so Check2GO brand tokens stay stable.
 */
@Composable
fun Check2GoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val tokens = if (darkTheme) {
        Check2GoTokens(Color(0xFF253C55), C2GDarkDisabledSurface, Color(0xFF3A4F66), Color(0xFF5DBB63), Color(0xFFE4A642), C2GTextSecondary)
    } else {
        Check2GoTokens(Color.White, Color(0xFFF4F7FA), Color(0xFFE5E7EB), Color(0xFF2E7D32), Color(0xFFB26A00), C2GTextSecondary)
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalCheck2GoTokens provides tokens) {
        MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
    }
}
