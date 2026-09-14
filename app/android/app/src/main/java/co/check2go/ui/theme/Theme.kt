package co.check2go.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = C2GPrimary,
    onPrimary = Color.White,
    background = C2GLightBackground,
    onBackground = C2GLightTextPrimary,
    surface = C2GLightBackground,
    onSurface = C2GLightTextPrimary,
    onSurfaceVariant = C2GTextSecondary,
    error = C2GError
)

private val DarkColors = darkColorScheme(
    primary = C2GPrimary,
    onPrimary = Color.White,
    background = C2GDarkBackground,
    onBackground = C2GDarkTextPrimary,
    surface = C2GDarkBackground,
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
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
