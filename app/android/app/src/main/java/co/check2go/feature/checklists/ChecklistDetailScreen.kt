package co.check2go.feature.checklists

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/**
 * Shared screen ID: CHECKLIST_DETAIL (docs/flows.md Flow 6; docs/screen-inventory.md "Checklist
 * Detail"). Reached by tapping a row on CHECKLISTS_POPULATED.
 *
 * Out of scope for this task: partner promos, notifications/deadlines, Delete/Share
 * (docs/screen-inventory.md lists these for the historical PDF screens, but the task scope excludes
 * them here), and real file attachment -- items with `includeFile` show only a temporary/unavailable
 * note, never a functioning picker/storage/backend. "Edit checklist" (docs/flows.md Flow 8) opens
 * CHECKLIST_EDIT via [onEditChecklist]. "Duplicate checklist" (docs/flows.md Flow 9) creates a copy
 * via [onDuplicateChecklist]; this screen does not perform the duplication itself, it only signals
 * the intent, since -- like completion toggling -- id assignment and where the copy is stored is
 * caller state.
 *
 * Stateless: [checklist] is the single source of truth for both the shown completion percentage and
 * grouping, and toggling delegates to [onToggleItem] so the caller (which also drives
 * CHECKLISTS_POPULATED from the same saved-checklist list) is the only place completion is stored.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistDetailScreen(
    checklist: CompletedChecklist,
    onToggleItem: (Long) -> Unit,
    onEditChecklist: () -> Unit,
    onDuplicateChecklist: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = checklist.name) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.trip_back))
                    }
                },
                actions = {
                    TextButton(onClick = onDuplicateChecklist) {
                        Text(text = stringResource(R.string.checklist_duplicate_action))
                    }
                    TextButton(onClick = onEditChecklist) {
                        Text(text = stringResource(R.string.checklist_edit_action))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.checklists_completion_format, checklist.completionPercent()),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.testTag("checklist_detail_completion")
            )

            if (checklist.items.isEmpty()) {
                Text(
                    text = stringResource(R.string.checklist_detail_no_items),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sections render in draft order (the order they were added during CHECKLIST_CREATE);
            // a section with no items is skipped rather than shown as an empty group.
            checklist.sections.forEach { section ->
                val sectionItems = checklist.items.filter { it.sectionId == section.id }
                if (sectionItems.isNotEmpty()) {
                    ChecklistItemGroup(title = section.name, items = sectionItems, onToggleItem = onToggleItem)
                }
            }

            // Items with no section, or whose sectionId doesn't match any section on this checklist
            // (defensive: should not happen since sections are only removed together with the
            // reference, see withSectionRemoved), are grouped together rather than dropped.
            val knownSectionIds = checklist.sections.map { it.id }.toSet()
            val unsectionedItems = checklist.items.filter { it.sectionId == null || it.sectionId !in knownSectionIds }
            if (unsectionedItems.isNotEmpty()) {
                ChecklistItemGroup(
                    title = stringResource(R.string.checklist_no_section),
                    items = unsectionedItems,
                    onToggleItem = onToggleItem
                )
            }
        }
    }
}

@Composable
private fun ChecklistItemGroup(
    title: String,
    items: List<SavedChecklistItem>,
    onToggleItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach { item ->
            ChecklistItemRow(item = item, onToggle = { onToggleItem(item.id) })
        }
    }
}

@Composable
private fun ChecklistItemRow(item: SavedChecklistItem, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag("checklist_detail_item_row_${item.id}")
                .toggleable(value = item.completed, onValueChange = { onToggle() }, role = Role.Checkbox)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox itself is non-interactive; the enclosing Row's toggleable() drives it so the
            // label and control share one large, one-handed accessible tap target (mirrors the
            // "Include file" switch pattern in ChecklistCreateScreen).
            Checkbox(checked = item.completed, onCheckedChange = null)
            Text(text = item.name, color = MaterialTheme.colorScheme.onBackground)
        }
        if (item.includeFile) {
            Text(
                text = stringResource(R.string.checklist_detail_upload_file_unavailable),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 12.dp)
                    .testTag("checklist_detail_upload_file_note_${item.id}")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChecklistDetailPreview() {
    Check2GoTheme(darkTheme = false) {
        ChecklistDetailScreen(
            checklist = CompletedChecklist(
                id = 1L,
                name = "Before leaving",
                sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
                items = listOf(
                    SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = true),
                    SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
                )
            ),
            onToggleItem = {},
            onEditChecklist = {},
            onDuplicateChecklist = {},
            onBack = {}
        )
    }
}
