package co.check2go.feature.home

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import co.check2go.R

enum class HomeDestination(@StringRes val labelRes: Int) {
    Home(R.string.nav_home),
    Documents(R.string.nav_documents),
    Checklists(R.string.nav_checklists),
    Events(R.string.nav_events)
}

/** Shared bottom navigation bar across the Home-tab screens (empty and populated My trips). */
@Composable
internal fun HomeNavigationBar(
    onDestinationSelected: (HomeDestination) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        HomeDestination.entries.forEach { destination ->
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                selected = destination == HomeDestination.Home,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    // Temporary glyph until the exact tab icon set is recovered from design assets.
                    Text(text = label.take(1), fontWeight = FontWeight.SemiBold)
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
