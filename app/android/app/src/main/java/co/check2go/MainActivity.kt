package co.check2go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import co.check2go.app.Check2GoApp
import co.check2go.core.localization.LocalizedContent
import co.check2go.ui.theme.Check2GoTheme
import co.check2go.ui.theme.ThemeContent
import co.check2go.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemeContent { mode, onToggleTheme ->
                SideEffect {
                    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = mode == ThemeMode.Light
                    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = mode == ThemeMode.Light
                }
                LocalizedContent { language, onLanguageChanged ->
                    Check2GoApp(
                        selectedLanguage = language,
                        onLanguageChanged = onLanguageChanged,
                        onToggleTheme = onToggleTheme,
                        isDarkTheme = mode == ThemeMode.Dark,
                        onTripCreateComplete = { _, _, _ -> }
                    )
                }
            }
        }
    }
}
