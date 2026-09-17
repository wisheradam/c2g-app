package co.check2go.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class ThemeMode(val storageValue: String) { Light("light"), Dark("dark") }

class ThemePreferenceStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    fun load(): ThemeMode? = when (preferences.getString(KEY_THEME, null)) {
        ThemeMode.Light.storageValue -> ThemeMode.Light
        ThemeMode.Dark.storageValue -> ThemeMode.Dark
        else -> null
    }
    fun save(mode: ThemeMode) = preferences.edit().putString(KEY_THEME, mode.storageValue).apply()

    private companion object {
        const val PREFERENCES = "check2go_theme"
        const val KEY_THEME = "theme_mode"
    }
}

/** Local-first theme provider. System appearance is used once, only until the user chooses. */
@Composable
fun ThemeContent(content: @Composable (ThemeMode, () -> Unit) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val store = remember(context) { ThemePreferenceStore(context) }
    val systemDefault = if (isSystemInDarkTheme()) ThemeMode.Dark else ThemeMode.Light
    var mode by remember { mutableStateOf(store.load() ?: systemDefault) }
    Check2GoTheme(darkTheme = mode == ThemeMode.Dark) {
        content(mode) {
            val next = if (mode == ThemeMode.Dark) ThemeMode.Light else ThemeMode.Dark
            store.save(next)
            mode = next
        }
    }
}
