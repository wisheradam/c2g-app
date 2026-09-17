package co.check2go.feature.checklists

import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Out of scope for this task: real file attachment, trip linkage, Use now, and
 * share/delete (docs/screen-inventory.md "Create / Edit Checklist" lists these, but the task scope
 * excludes them here). "Duplicate checklist" (docs/flows.md Flow 9) is in scope but only meaningful
 * once something is actually saved to duplicate, so [onDuplicateChecklist] is nullable and the
 * caller only supplies it for CHECKLIST_EDIT (an existing saved checklist), never for
 * CHECKLIST_CREATE (nothing saved yet); the action is hidden when null.
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
    title: String = stringResource(R.string.checklist_create_title),
    onDuplicateChecklist: (() -> Unit)? = null
) {
    var showPhotoSheet by rememberSaveable { mutableStateOf(false) }
    val canSave = draft.name.isNotBlank() &&
        draft.sections.all { it.name.isNotBlank() } &&
        draft.items.all { it.name.isNotBlank() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF4F7FA),
        topBar = {
            ChecklistEditorHeader(title, onBack)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.trip_destination_malaga),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Replace photo",
                    modifier = Modifier.padding(top = 8.dp).clickable { showPhotoSheet = true }
                        .testTag("checklist_replace_photo"),
                    color = Color(0xFF043CB3),
                    fontSize = 16.sp
                )
            }

            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(10.dp)
            ) {
                Text(stringResource(R.string.checklist_name_label), color = Color(0xFF002349), fontSize = 16.sp)
                OutlinedTextField(
                    value = draft.name, onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth().testTag("checklist_name_field"),
                    placeholder = { Text("Checklist name", color = Color(0xFF8D919A)) },
                    singleLine = true, shape = RoundedCornerShape(11.dp)
                )
                Text("You can create your own title", color = Color(0xFFB9BBC1), fontSize = 12.sp)
                Text("Link with trip", Modifier.padding(top = 10.dp), color = Color(0xFF002349), fontSize = 16.sp)
                OutlinedTextField(
                    value = "London - Tel Aviv", onValueChange = {}, readOnly = true,
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(11.dp),
                    trailingIcon = { Text("⌄", color = Color(0xFF8D919A), fontSize = 20.sp) }
                )
                Text("Link this checklist to your trip and it will appear in your trip\ndetails", color = Color(0xFFB9BBC1), fontSize = 12.sp, lineHeight = 14.sp)
            }

            ItemsList(
                items = draft.items, sections = draft.sections,
                onItemNameChange = onItemNameChange,
                onItemSectionChange = onItemSectionChange,
                onItemIncludeFileChange = onItemIncludeFileChange,
                onRemoveItem = onRemoveItem
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

            ChecklistEditorAction("☷", "Use now")
            ChecklistEditorAction("↗", "Share checklist")
            if (onDuplicateChecklist != null) {
                ChecklistEditorAction("▣", stringResource(R.string.checklist_duplicate_action), onDuplicateChecklist)
            }
            ChecklistEditorAction("♙", "Delete checklist", color = Color(0xFFC23C68))

            SectionsEditor(
                sections = draft.sections,
                newSectionName = newSectionName,
                onNewSectionNameChange = onNewSectionNameChange,
                onAddSection = onAddSection,
                onSectionNameChange = onSectionNameChange,
                onRemoveSection = onRemoveSection
            )

            Box(
                Modifier.fillMaxWidth().height(43.dp).clip(RoundedCornerShape(10.dp))
                    .background(if (canSave) Color(0xFF043CB3) else Color(0xFFB8C2D2))
                    .clickable(enabled = canSave, onClick = onSave),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.checklist_save), color = Color.White, fontSize = 16.sp)
            }
        }
    }

    if (showPhotoSheet) {
        ChecklistReplacePhotoSheet(onDismiss = { showPhotoSheet = false })
    }
}

/** Approved Figma checklist photo sheet 1:28396. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChecklistReplacePhotoSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = {
            Box(
                Modifier.padding(top = 10.dp, bottom = 18.dp).size(40.dp, 4.dp)
                    .clip(RoundedCornerShape(2.dp)).background(Color.White)
            )
        },
        modifier = Modifier.testTag("checklist_photo_sheet")
    ) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 18.dp, vertical = 4.dp)
        ) {
            Text(
                "Replace photo",
                Modifier.fillMaxWidth(),
                color = Color(0xFF002349),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Image(
                painterResource(R.drawable.trip_destination_malaga),
                null,
                Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally).size(44.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F1F3))
            ChecklistPhotoAction("▧", "Choose from gallery", Color(0xFF002349), "checklist_photo_gallery")
            Spacer(Modifier.height(20.dp))
            ChecklistPhotoAction("▣", "Make a photo", Color(0xFF002349), "checklist_photo_camera")
            Spacer(Modifier.height(20.dp))
            ChecklistPhotoAction("♙", "Delete photo", Color(0xFFC23C68), "checklist_photo_delete")
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ChecklistPhotoAction(icon: String, label: String, color: Color, tag: String) {
    Row(Modifier.fillMaxWidth().height(28.dp).testTag(tag), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = color, fontSize = 20.sp, modifier = Modifier.width(30.dp))
        Text(label, color = color, fontSize = 17.sp)
    }
}

@Composable
private fun ChecklistEditorHeader(title: String, onBack: () -> Unit) {
    val muted = Color(0xFF8D919A)
    val backLabel = stringResource(R.string.trip_back)
    Row(
        Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.c2g_arrow_left), null,
            Modifier.size(20.dp).clickable(onClick = onBack).semantics { text = AnnotatedString(backLabel) },
            colorFilter = ColorFilter.tint(muted)
        )
        Spacer(Modifier.weight(1f)); Text(title, color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f))
        listOf(R.drawable.c2g_settings, R.drawable.c2g_help, R.drawable.c2g_notification).forEachIndexed { index, icon ->
            if (index > 0) Spacer(Modifier.width(12.dp))
            Image(painterResource(icon), null, Modifier.size(21.dp), colorFilter = ColorFilter.tint(muted))
        }
    }
}

@Composable
private fun ChecklistEditorAction(icon: String, label: String, onClick: (() -> Unit)? = null, color: Color = Color(0xFF002349)) {
    Row(
        Modifier.fillMaxWidth().height(25.dp).clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, color = color, fontSize = 21.sp, modifier = Modifier.width(32.dp))
        Text(label, color = color, fontSize = 17.sp)
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
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newItemName, onValueChange = onNewItemNameChange,
                modifier = Modifier.weight(1f).testTag("new_item_name_field"),
                placeholder = { Text(stringResource(R.string.checklist_item_name_label), color = Color(0xFF8D919A)) },
                singleLine = true, shape = RoundedCornerShape(10.dp)
            )
            IncludeFileToggleRow(
                checked = newItemIncludeFile,
                onCheckedChange = onNewItemIncludeFileChange,
                modifier = Modifier.width(104.dp).testTag("new_item_include_file_toggle")
            )
        }

        if (sections.isNotEmpty()) SectionDropdownField(
            sections, newItemSectionId, onNewItemSectionIdChange, "new_item_section_field"
        )

        Box(
            Modifier.align(Alignment.End).height(36.dp).clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF0F3F7)).clickable(enabled = newItemName.isNotBlank(), onClick = onAddItem)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("+", color = Color(0xFF043CB3), fontSize = 20.sp)
                Spacer(Modifier.width(7.dp))
                Text(stringResource(R.string.checklist_add_item), color = Color(0xFF043CB3), fontSize = 16.sp)
            }
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
