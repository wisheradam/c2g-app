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
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
 * Shared screen ID: CHECKLIST_CREATE (docs/flows.md Flow 7; docs/screen-inventory.md "Create /
 * Edit Checklist"). Both the CHECKLISTS_EMPTY "Create checklist" CTA and the shared quick-add FAB
 * open this stateless screen.
 *
 * Out of scope for this task: checklist photo, real file attachment, trip linkage, Use now, and
 * share/duplicate/delete (docs/screen-inventory.md "Create / Edit Checklist" lists these, but the
 * task scope excludes them here).
 *
 * All draft mutation is delegated to the caller: this composable only reads [draft] and the
 * transient new-item form fields, and forwards user intents through callbacks. It performs no
 * draft transformation itself.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistCreateScreen(
    draft: ChecklistDraft,
    onNameChange: (String) -> Unit,
    newSectionName: String,
    onNewSectionNameChange: (String) -> Unit,
    onAddSection: () -> Unit,
    onRemoveSection: (Long) -> Unit,
    newItemName: String,
    onNewItemNameChange: (String) -> Unit,
    newItemSectionId: Long?,
    onNewItemSectionIdChange: (Long?) -> Unit,
    newItemIncludeFile: Boolean,
    onNewItemIncludeFileChange: (Boolean) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Long) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.checklist_create_title)) },
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
                onRemoveItem = onRemoveItem
            )

            Button(
                onClick = onSave,
                enabled = draft.name.isNotBlank(),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = section.name, color = MaterialTheme.colorScheme.onBackground)
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
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.checklist_item_name_label)) },
            singleLine = true
        )

        val noSectionLabel = stringResource(R.string.checklist_no_section)
        val selectedSectionLabel = sections.firstOrNull { it.id == newItemSectionId }?.name ?: noSectionLabel
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedSectionLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(R.string.checklist_item_section_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_item_section_field")
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = noSectionLabel) },
                    onClick = {
                        onNewItemSectionIdChange(null)
                        expanded = false
                    }
                )
                sections.forEach { section ->
                    DropdownMenuItem(
                        text = { Text(text = section.name) },
                        onClick = {
                            onNewItemSectionIdChange(section.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = newItemIncludeFile,
                    onValueChange = onNewItemIncludeFileChange,
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
            Switch(checked = newItemIncludeFile, onCheckedChange = null)
        }

        OutlinedButton(
            onClick = onAddItem,
            enabled = newItemName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.checklist_add_item))
        }
    }
}

@Composable
private fun ItemsList(
    items: List<ChecklistItemDraft>,
    sections: List<ChecklistSection>,
    onRemoveItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val noSectionLabel = stringResource(R.string.checklist_no_section)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.checklist_items_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach { item ->
            val sectionLabel = sections.firstOrNull { it.id == item.sectionId }?.name ?: noSectionLabel
            val removeLabel = stringResource(R.string.checklist_remove_item_description, item.name)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_item_row_${item.id}")
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(
                        onClick = { onRemoveItem(item.id) },
                        modifier = Modifier.semantics { contentDescription = removeLabel }
                    ) {
                        Text(text = stringResource(R.string.checklist_remove))
                    }
                }
                Text(
                    text = sectionLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("checklist_item_section_label_${item.id}")
                )
                if (item.includeFile) {
                    Text(
                        text = stringResource(R.string.checklist_item_includes_file_badge),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
            onRemoveSection = {},
            newItemName = "",
            onNewItemNameChange = {},
            newItemSectionId = null,
            onNewItemSectionIdChange = {},
            newItemIncludeFile = false,
            onNewItemIncludeFileChange = {},
            onAddItem = {},
            onRemoveItem = {},
            onBack = {},
            onSave = {}
        )
    }
}
