package co.check2go.core.design

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
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

private val AppDestination.icon: ImageVector
    get() = when (this) {
        AppDestination.Home -> Icons.Outlined.Home
        AppDestination.Documents -> Icons.Outlined.Description
        AppDestination.Checklists -> Icons.Outlined.Checklist
        AppDestination.Events -> Icons.Outlined.CalendarMonth
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
                    Icon(imageVector = destination.icon, contentDescription = null)
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
