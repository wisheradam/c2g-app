package co.check2go.feature.checklists

import androidx.compose.runtime.saveable.Saver

/** A named group that items can optionally be associated with. [id] is stable and local-only. */
data class ChecklistSection(val id: Long, val name: String)

/**
 * A single draft checklist item. [sectionId] is null when the item has no section association;
 * otherwise it references a [ChecklistSection.id] within the same [ChecklistDraft]. [includeFile]
 * only records intent (docs/flows.md Flow 7, step 6): no actual file attachment is implemented.
 */
data class ChecklistItemDraft(
    val id: Long,
    val name: String,
    val sectionId: Long?,
    val includeFile: Boolean
)

/**
 * Stateless draft for the shared CHECKLIST_CREATE screen (docs/flows.md Flow 7;
 * docs/screen-inventory.md "Create / Edit Checklist"). Checklist photo and trip linkage are out of
 * scope for this task, so this draft only models name, sections and items.
 */
data class ChecklistDraft(
    val name: String = "",
    val sections: List<ChecklistSection> = emptyList(),
    val items: List<ChecklistItemDraft> = emptyList()
)

/** Appends [section] to the draft. Pure transformation kept outside the rendering composable. */
fun ChecklistDraft.withSectionAdded(section: ChecklistSection): ChecklistDraft =
    copy(sections = sections + section)

/**
 * Removes the section with [sectionId] and clears that association from any item that referenced
 * it, rather than deleting those items.
 */
fun ChecklistDraft.withSectionRemoved(sectionId: Long): ChecklistDraft = copy(
    sections = sections.filterNot { it.id == sectionId },
    items = items.map { item ->
        if (item.sectionId == sectionId) item.copy(sectionId = null) else item
    }
)

/** Appends [item] to the draft. Pure transformation kept outside the rendering composable. */
fun ChecklistDraft.withItemAdded(item: ChecklistItemDraft): ChecklistDraft =
    copy(items = items + item)

/** Removes the item with [itemId] from the draft. */
fun ChecklistDraft.withItemRemoved(itemId: Long): ChecklistDraft =
    copy(items = items.filterNot { it.id == itemId })

// Control characters rather than normal punctuation, so they cannot collide with user-entered
// checklist/section/item names. Distinct from CompletedTripListSaver's separator since these are
// independent encodings.
private val FIELD_SEPARATOR: String = Char(1).toString()
private val RECORD_SEPARATOR: String = Char(2).toString()

/**
 * Encodes a [ChecklistDraft] as a 3-element string list so it can survive recomposition/
 * configuration changes via `rememberSaveable` without adding a Parcelize dependency. This is a
 * saveable-state representation only, not durable persistence: the draft is lost on process death
 * / fresh app relaunch (see docs/android/architecture.md "Data direction").
 */
val ChecklistDraftSaver: Saver<ChecklistDraft, ArrayList<String>> = Saver(
    save = { draft ->
        arrayListOf(
            draft.name,
            draft.sections.joinToString(RECORD_SEPARATOR) { it.encode() },
            draft.items.joinToString(RECORD_SEPARATOR) { it.encode() }
        )
    },
    restore = { encoded ->
        ChecklistDraft(
            name = encoded[0],
            sections = encoded[1].splitRecords().map { it.decodeSection() },
            items = encoded[2].splitRecords().map { it.decodeItem() }
        )
    }
)

private fun String.splitRecords(): List<String> = if (isEmpty()) emptyList() else split(RECORD_SEPARATOR)

private fun ChecklistSection.encode(): String = listOf(id.toString(), name).joinToString(FIELD_SEPARATOR)

private fun String.decodeSection(): ChecklistSection {
    val fields = split(FIELD_SEPARATOR)
    return ChecklistSection(id = fields[0].toLong(), name = fields[1])
}

private fun ChecklistItemDraft.encode(): String = listOf(
    id.toString(),
    name,
    sectionId?.toString().orEmpty(),
    includeFile.toString()
).joinToString(FIELD_SEPARATOR)

private fun String.decodeItem(): ChecklistItemDraft {
    val fields = split(FIELD_SEPARATOR)
    return ChecklistItemDraft(
        id = fields[0].toLong(),
        name = fields[1],
        sectionId = fields[2].ifEmpty { null }?.toLong(),
        includeFile = fields[3].toBoolean()
    )
}
