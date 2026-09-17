package co.check2go.core.design

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import co.check2go.R

/** Bottom navigation destinations shared by every main-tab screen (HOME_EMPTY/HOME_TRIPS, CHECKLISTS_EMPTY). */
enum class AppDestination(@StringRes val labelRes: Int) {
    Home(R.string.nav_home),
    Documents(R.string.nav_documents),
    Checklists(R.string.nav_checklists),
    Events(R.string.nav_events)
}

private val AppDestination.icon: Int
    get() = when (this) {
        AppDestination.Home -> R.drawable.c2g_home
        AppDestination.Documents -> R.drawable.c2g_documents
        AppDestination.Checklists -> R.drawable.c2g_checklists
        AppDestination.Events -> R.drawable.c2g_events
    }

/**
 * Shared bottom navigation bar across main-tab screens. [selected] must reflect the caller's
 * actual current tab rather than a hardcoded value, so the indicator stays correct as more tabs
 * become functional.
 */
@Composable
internal fun AppNavigationBar(
    selected: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        AppDestination.entries.forEach { destination ->
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                modifier = Modifier.testTag("nav_${destination.name.lowercase()}"),
                selected = destination == selected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Image(
                        painter = painterResource(destination.icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            if (destination == selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                },
                label = { Text(text = label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
