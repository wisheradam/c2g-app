package co.check2go.feature.checklists

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppNavigationBar
import co.check2go.ui.theme.Check2GoTheme

/**
 * Shared screen ID: CHECKLISTS_POPULATED (docs/flows.md Flow 5, step 4;
 * docs/screen-inventory.md "Checklists — main tab > Populated list").
 *
 * Stateless: shows each checklist's name, a neutral personal context (trip linkage is out of
 * scope, see [CompletedChecklist]) and its calculated completion percentage
 * ([completionPercent]). Tapping a row invokes [onChecklistSelected] to open CHECKLIST_DETAIL.
 * Assumes [checklists] is non-empty; the caller shows [ChecklistsEmptyScreen] instead while there
 * are none.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistsPopulatedScreen(
    checklists: List<CompletedChecklist>,
    onCreateChecklist: () -> Unit,
    onQuickAdd: () -> Unit,
    onChecklistSelected: (Long) -> Unit,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onCreateChecklist, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.checklists_create_checklist))
            }
            checklists.forEach { checklist ->
                ChecklistCard(checklist = checklist, onClick = { onChecklistSelected(checklist.id) })
            }
        }
    }
}

@Composable
private fun ChecklistCard(checklist: CompletedChecklist, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("checklist_card_${checklist.id}")
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
            .clickable(onClickLabel = checklist.name, onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = checklist.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.checklists_personal_context),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = stringResource(R.string.checklists_completion_format, checklist.completionPercent()),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChecklistsPopulatedPreview() {
    Check2GoTheme(darkTheme = false) {
        ChecklistsPopulatedScreen(
            checklists = listOf(
                CompletedChecklist(
                    id = 1L,
                    name = "Before leaving",
                    items = listOf(
                        SavedChecklistItem(id = 1L, name = "Passport", sectionId = null, includeFile = true, completed = true),
                        SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
                    )
                ),
                CompletedChecklist(id = 2L, name = "Documents")
            ),
            onCreateChecklist = {},
            onQuickAdd = {},
            onChecklistSelected = {},
            onDestinationSelected = {}
        )
    }
}
