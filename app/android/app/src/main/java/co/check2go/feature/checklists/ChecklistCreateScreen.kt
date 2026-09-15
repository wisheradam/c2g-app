package co.check2go.feature.checklists

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/**
 * Shared editor for both CHECKLIST_CREATE (docs/flows.md Flow 7) and CHECKLIST_EDIT (docs/flows.md
 * Flow 8), matching docs/screen-inventory.md's single "Create / Edit Checklist" screen. The
 * CHECKLISTS_EMPTY "Create checklist" CTA and the shared quick-add FAB open this screen with an
 * empty [draft]; the CHECKLIST_DETAIL "Edit checklist" action opens it with [draft] prefilled from
 * the saved checklist. [title] is the only thing that distinguishes the two call sites in this
 * composable; all other caller-side differences (draft seeding, where "Save changes" writes to)
 * live in `Check2GoApp`.
 *
 * Out of scope for this task: checklist photo, real file attachment, trip linkage, Use now, and
 * share/duplicate/delete (docs/screen-inventory.md "Create / Edit Checklist" lists these, but the
 * task scope excludes them here).
 *
 * All draft mutation is delegated to the caller: this composable only reads [draft] and the
 * transient new-item form fields, and forwards user intents through callbacks. It performs no
 * draft transformation itself.
 *
 * Existing sections/items are editable in place (docs/flows.md Flow 8, step 3), not just
 * addable/removable: [onSectionNameChange] renames an existing section, and
 * [onItemNameChange]/[onItemSectionChange]/[onItemIncludeFileChange] edit an existing item's name,
 * section assignment and include-file flag respectively. All four keep the edited section/item's id
 * stable, which is what lets CHECKLIST_EDIT saves preserve completion (see
 * [co.check2go.feature.checklists.withDraftApplied]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistCreateScreen(
    draft: ChecklistDraft,
    onNameChange: (String) -> Unit,
    newSectionName: String,
    onNewSectionNameChange: (String) -> Unit,
    onAddSection: () -> Unit,
    onSectionNameChange: (Long, String) -> Unit,
    onRemoveSection: (Long) -> Unit,
    newItemName: String,
    onNewItemNameChange: (String) -> Unit,
    newItemSectionId: Long?,
    onNewItemSectionIdChange: (Long?) -> Unit,
    newItemIncludeFile: Boolean,
    onNewItemIncludeFileChange: (Boolean) -> Unit,
    onAddItem: () -> Unit,
    onItemNameChange: (Long, String) -> Unit,
    onItemSectionChange: (Long, Long?) -> Unit,
    onItemIncludeFileChange: (Long, Boolean) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.checklist_create_title)
) {
    val canSave = draft.name.isNotBlank() &&
        draft.sections.all { it.name.isNotBlank() } &&
        draft.items.all { it.name.isNotBlank() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.trip_back))
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
            OutlinedTextField(
                value = draft.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.checklist_name_label)) },
                singleLine = true
            )

            SectionsEditor(
                sections = draft.sections,
                newSectionName = newSectionName,
                onNewSectionNameChange = onNewSectionNameChange,
                onAddSection = onAddSection,
                onSectionNameChange = onSectionNameChange,
                onRemoveSection = onRemoveSection
            )

            NewItemEditor(
                sections = draft.sections,
                newItemName = newItemName,
                onNewItemNameChange = onNewItemNameChange,
                newItemSectionId = newItemSectionId,
                onNewItemSectionIdChange = onNewItemSectionIdChange,
                newItemIncludeFile = newItemIncludeFile,
                onNewItemIncludeFileChange = onNewItemIncludeFileChange,
                onAddItem = onAddItem
            )

            ItemsList(
                items = draft.items,
                sections = draft.sections,
                onItemNameChange = onItemNameChange,
                onItemSectionChange = onItemSectionChange,
                onItemIncludeFileChange = onItemIncludeFileChange,
                onRemoveItem = onRemoveItem
            )

            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.checklist_save))
            }
        }
    }
}

@Composable
private fun SectionsEditor(
    sections: List<ChecklistSection>,
    newSectionName: String,
    onNewSectionNameChange: (String) -> Unit,
    onAddSection: () -> Unit,
    onSectionNameChange: (Long, String) -> Unit,
    onRemoveSection: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.checklist_sections_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        sections.forEach { section ->
            val removeLabel = stringResource(R.string.checklist_remove_section_description, section.name)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_section_row_${section.id}")
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = section.name,
                    onValueChange = { onSectionNameChange(section.id, it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("checklist_section_name_field_${section.id}"),
                    label = { Text(text = stringResource(R.string.checklist_section_name_label)) },
                    singleLine = true
                )
                TextButton(
                    onClick = { onRemoveSection(section.id) },
                    modifier = Modifier.semantics { contentDescription = removeLabel }
                ) {
                    Text(text = stringResource(R.string.checklist_remove))
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newSectionName,
                onValueChange = onNewSectionNameChange,
                modifier = Modifier.weight(1f),
                label = { Text(text = stringResource(R.string.checklist_section_name_label)) },
                singleLine = true
            )
            Button(onClick = onAddSection, enabled = newSectionName.isNotBlank()) {
                Text(text = stringResource(R.string.checklist_add_section))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewItemEditor(
    sections: List<ChecklistSection>,
    newItemName: String,
    onNewItemNameChange: (String) -> Unit,
    newItemSectionId: Long?,
    onNewItemSectionIdChange: (Long?) -> Unit,
    newItemIncludeFile: Boolean,
    onNewItemIncludeFileChange: (Boolean) -> Unit,
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.checklist_add_item_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = newItemName,
            onValueChange = onNewItemNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("new_item_name_field"),
            label = { Text(text = stringResource(R.string.checklist_item_name_label)) },
            singleLine = true
        )

        SectionDropdownField(
            sections = sections,
            selectedSectionId = newItemSectionId,
            onSectionSelected = onNewItemSectionIdChange,
            testTag = "new_item_section_field"
        )

        IncludeFileToggleRow(
            checked = newItemIncludeFile,
            onCheckedChange = onNewItemIncludeFileChange,
            modifier = Modifier.testTag("new_item_include_file_toggle")
        )

        OutlinedButton(
            onClick = onAddItem,
            enabled = newItemName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.checklist_add_item))
        }
    }
}

/**
 * Section picker shared by [NewItemEditor]'s new-item form and [ItemsList]'s existing-item rows,
 * so both editing paths present the same "No section" + known-sections choice.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionDropdownField(
    sections: List<ChecklistSection>,
    selectedSectionId: Long?,
    onSectionSelected: (Long?) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val noSectionLabel = stringResource(R.string.checklist_no_section)
    val selectedSectionLabel = sections.firstOrNull { it.id == selectedSectionId }?.name ?: noSectionLabel
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedSectionLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.checklist_item_section_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Explicit per-option testTags (rather than matching menu items by their displayed
            // text) so a section whose name happens to match other on-screen text -- e.g. its own
            // now-editable name field in SectionsEditor -- can never make a test's menu-item click
            // ambiguous.
            DropdownMenuItem(
                text = { Text(text = noSectionLabel) },
                onClick = {
                    onSectionSelected(null)
                    expanded = false
                },
                modifier = Modifier.testTag("${testTag}_option_none")
            )
            sections.forEach { section ->
                DropdownMenuItem(
                    text = { Text(text = section.name) },
                    onClick = {
                        onSectionSelected(section.id)
                        expanded = false
                    },
                    modifier = Modifier.testTag("${testTag}_option_${section.id}")
                )
            }
        }
    }
}

/**
 * Include-file toggle shared by [NewItemEditor]'s new-item form and [ItemsList]'s existing-item
 * rows.
 */
@Composable
private fun IncludeFileToggleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.checklist_include_file_label),
            color = MaterialTheme.colorScheme.onBackground
        )
        // Switch itself is non-interactive; the enclosing Row's toggleable() drives it so the
        // label and control share one accessible tap target and announcement.
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
private fun ItemsList(
    items: List<ChecklistItemDraft>,
    sections: List<ChecklistSection>,
    onItemNameChange: (Long, String) -> Unit,
    onItemSectionChange: (Long, Long?) -> Unit,
    onItemIncludeFileChange: (Long, Boolean) -> Unit,
    onRemoveItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.checklist_items_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach { item ->
            val removeLabel = stringResource(R.string.checklist_remove_item_description, item.name)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_item_row_${item.id}")
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = item.name,
                        onValueChange = { onItemNameChange(item.id, it) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("checklist_item_name_field_${item.id}"),
                        label = { Text(text = stringResource(R.string.checklist_item_name_label)) },
                        singleLine = true
                    )
                    TextButton(
                        onClick = { onRemoveItem(item.id) },
                        modifier = Modifier.semantics { contentDescription = removeLabel }
                    ) {
                        Text(text = stringResource(R.string.checklist_remove))
                    }
                }
                SectionDropdownField(
                    sections = sections,
                    selectedSectionId = item.sectionId,
                    onSectionSelected = { onItemSectionChange(item.id, it) },
                    testTag = "checklist_item_section_field_${item.id}"
                )
                IncludeFileToggleRow(
                    checked = item.includeFile,
                    onCheckedChange = { onItemIncludeFileChange(item.id, it) },
                    modifier = Modifier.testTag("checklist_item_include_file_toggle_${item.id}")
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChecklistCreatePreview() {
    Check2GoTheme(darkTheme = false) {
        ChecklistCreateScreen(
            draft = ChecklistDraft(
                name = "Before leaving",
                sections = listOf(ChecklistSection(id = 1L, name = "Documents")),
                items = listOf(
                    ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 1L, includeFile = true),
                    ChecklistItemDraft(id = 2L, name = "Charger", sectionId = null, includeFile = false)
                )
            ),
            onNameChange = {},
            newSectionName = "",
            onNewSectionNameChange = {},
            onAddSection = {},
            onSectionNameChange = { _, _ -> },
            onRemoveSection = {},
            newItemName = "",
            onNewItemNameChange = {},
            newItemSectionId = null,
            onNewItemSectionIdChange = {},
            newItemIncludeFile = false,
            onNewItemIncludeFileChange = {},
            onAddItem = {},
            onItemNameChange = { _, _ -> },
            onItemSectionChange = { _, _ -> },
            onItemIncludeFileChange = { _, _ -> },
            onRemoveItem = {},
            onBack = {},
            onSave = {}
        )
    }
}
