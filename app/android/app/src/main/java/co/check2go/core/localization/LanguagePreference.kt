package co.check2go.core.localization

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * The app currently bundles these two languages. Keep the stable wire value when this preference
 * is later synchronised with a profile or sent as an Accept-Language request.
 */
enum class AppLanguage(val code: String, val nativeName: String) {
    English("en", "English"),
    Russian("ru", "Русский");

    val locale: Locale get() = Locale.forLanguageTag(code)

    companion object {
        fun fromCode(code: String?) = entries.firstOrNull { it.code == code }
    }
}

class LanguagePreferenceStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun load(): AppLanguage = AppLanguage.fromCode(preferences.getString(KEY_LANGUAGE, null))
        ?: languageForDevice(Locale.getDefault())

    fun save(language: AppLanguage) {
        preferences.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    companion object {
        private const val PREFERENCES = "check2go_language"
        private const val KEY_LANGUAGE = "interface_language"

        fun languageForDevice(locale: Locale): AppLanguage = when (locale.language.lowercase(Locale.ROOT)) {
            "ru" -> AppLanguage.Russian
            else -> AppLanguage.English
        }
    }
}

/** Supplies a locale-configured Android context to every stringResource call below it. */
@Composable
fun LocalizedContent(content: @Composable (AppLanguage, (AppLanguage) -> Unit) -> Unit) {
    val appContext = LocalContext.current.applicationContext
    val store = remember(appContext) { LanguagePreferenceStore(appContext) }
    var language by remember { mutableStateOf(store.load()) }
    val localizedContext = remember(language, appContext) {
        val configuration = Configuration(appContext.resources.configuration).apply { setLocale(language.locale) }
        appContext.createConfigurationContext(configuration)
    }
    CompositionLocalProvider(LocalContext provides localizedContext) {
        content(language) { selected ->
            if (selected != language) {
                store.save(selected)
                language = selected
            }
        }
    }
}
