package co.check2go.feature.checklists

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppNavigationBar
import co.check2go.ui.theme.Check2GoTheme

/**
 * Shared screen ID: CHECKLISTS_EMPTY (docs/screen-inventory.md "Checklists — main tab > Empty
 * state"; docs/flows.md Flow 5, steps 1-3).
 *
 * Stateless: navigation and checklist creation are supplied by the caller. [onCreateChecklist] and
 * [onQuickAdd] intentionally reach the same temporary callback boundary, since CHECKLIST_CREATE
 * (Flow 7) is out of scope for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistsEmptyScreen(
    onCreateChecklist: () -> Unit,
    onQuickAdd: () -> Unit,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAddLabel = stringResource(R.string.home_quick_add)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.checklists_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            AppNavigationBar(
                selected = AppDestination.Checklists,
                onDestinationSelected = onDestinationSelected
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQuickAdd,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.semantics {
                    contentDescription = quickAddLabel
                    role = Role.Button
                }
            ) {
                Text(text = "+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IllustrationPlaceholder()
            Text(
                text = stringResource(R.string.checklists_empty_hero_title),
                modifier = Modifier.padding(top = 32.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.checklists_empty_supporting_copy),
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onCreateChecklist,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(text = stringResource(R.string.checklists_create_checklist))
            }
            TeamChecklistsPromo(modifier = Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun IllustrationPlaceholder() {
    Box(
        modifier = Modifier
            .size(width = 208.dp, height = 144.dp)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.checklists_illustration_placeholder),
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Neutral, non-transactional placeholder for the "Team checklists" promo referenced in
 * docs/screen-inventory.md (Checklists empty state) and docs/flows.md Flow 11. The historical
 * design shows a $5/month, 7-day-trial upsell (docs/screen-inventory.md section 9); that pricing
 * and entitlement flow is unapproved, so this copy carries no price, subscription action, or
 * availability claim.
 */
@Composable
private fun TeamChecklistsPromo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.checklists_team_promo_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.checklists_team_promo_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChecklistsEmptyLightPreview() {
    Check2GoTheme(darkTheme = false) {
        ChecklistsEmptyScreen({}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ChecklistsEmptyDarkPreview() {
    Check2GoTheme(darkTheme = true) {
        ChecklistsEmptyScreen({}, {}, {})
    }
}
