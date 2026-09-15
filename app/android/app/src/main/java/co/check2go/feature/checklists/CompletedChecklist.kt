package co.check2go.feature.checklists

import androidx.compose.runtime.saveable.Saver

/**
 * Saved counterpart of [ChecklistSection]. Same id/name shape as the draft-time section, but
 * scoped to a persisted [CompletedChecklist] rather than an in-progress [ChecklistDraft], so a
 * later CHECKLIST_CREATE edit can never reach back and mutate already-saved checklist state.
 */
data class SavedChecklistSection(val id: Long, val name: String)

/**
 * Saved counterpart of [ChecklistItemDraft]. Adds [completed], which only exists once a checklist
 * is saved and opened in CHECKLIST_DETAIL (docs/flows.md Flow 6, step 3) -- the CHECKLIST_CREATE
 * editor has no completion concept, so that field cannot leak backwards into draft mutation.
 */
data class SavedChecklistItem(
    val id: Long,
    val name: String,
    val sectionId: Long?,
    val includeFile: Boolean,
    val completed: Boolean
)

/**
 * Smallest immutable full checklist needed to render CHECKLIST_DETAIL (docs/screen-inventory.md
 * "Checklist Detail") and calculated completion on CHECKLISTS_POPULATED (docs/flows.md Flow 5,
 * step 4). Local-only: no Room/backend persistence, see docs/android/architecture.md "Data
 * direction". Trip linking/context is out of scope for this task, so every checklist currently has
 * the same neutral personal context (docs/screen-inventory.md "Checklists — main tab").
 *
 * Deliberately a distinct type from [ChecklistDraft]/[ChecklistItemDraft] (the CHECKLIST_CREATE
 * editor model) rather than reusing them directly: saved state, including completion, must not be
 * reachable from -- or coupled to -- in-progress editor mutation.
 */
data class CompletedChecklist(
    val id: Long,
    val name: String,
    val sections: List<SavedChecklistSection> = emptyList(),
    val items: List<SavedChecklistItem> = emptyList()
)

/**
 * Converts a finished [ChecklistDraft] (CHECKLIST_CREATE "Save changes", Flow 7 step 7) into the
 * smallest immutable saved checklist, assigning it [id] and starting every item uncompleted.
 */
fun ChecklistDraft.toSavedChecklist(id: Long): CompletedChecklist = CompletedChecklist(
    id = id,
    name = name,
    sections = sections.map { SavedChecklistSection(id = it.id, name = it.name) },
    items = items.map { item ->
        SavedChecklistItem(
            id = item.id,
            name = item.name,
            sectionId = item.sectionId,
            includeFile = item.includeFile,
            completed = false
        )
    }
)

/**
 * Prefill for CHECKLIST_EDIT (docs/flows.md Flow 8, step 2): the inverse of [toSavedChecklist].
 * Item/section ids are carried over unchanged so a later [withDraftApplied] can match edited items
 * back to their saved completion state by id.
 */
fun CompletedChecklist.toDraft(): ChecklistDraft = ChecklistDraft(
    name = name,
    sections = sections.map { ChecklistSection(id = it.id, name = it.name) },
    items = items.map { item ->
        ChecklistItemDraft(id = item.id, name = item.name, sectionId = item.sectionId, includeFile = item.includeFile)
    }
)

/**
 * Applies an edited [draft] (CHECKLIST_EDIT "Save changes", Flow 8 step 4) back onto this saved
 * checklist, keeping [id] fixed. An item id already present on this checklist is logically
 * unchanged and keeps its current completion state even if its name/section changed in the editor;
 * an item id that is new to this checklist starts uncompleted (mirrors [toSavedChecklist]); an item
 * id from this checklist that is absent from [draft] was removed in the editor and is dropped.
 */
fun CompletedChecklist.withDraftApplied(draft: ChecklistDraft): CompletedChecklist {
    val completionById = items.associate { it.id to it.completed }
    return copy(
        name = draft.name,
        sections = draft.sections.map { SavedChecklistSection(id = it.id, name = it.name) },
        items = draft.items.map { item ->
            SavedChecklistItem(
                id = item.id,
                name = item.name,
                sectionId = item.sectionId,
                includeFile = item.includeFile,
                completed = completionById[item.id] ?: false
            )
        }
    )
}

/** Flips [itemId]'s completion. Pure transformation kept outside the rendering composable. */
fun CompletedChecklist.withItemCompletionToggled(itemId: Long): CompletedChecklist = copy(
    items = items.map { item -> if (item.id == itemId) item.copy(completed = !item.completed) else item }
)

/**
 * Deterministic 0-100 completion percentage (docs/flows.md Flow 6, step 4). An empty checklist is
 * defined as 0% rather than 100%/NaN. Uses integer division (rounded down) instead of Float/Double
 * math so the result is exact and stable rather than depending on floating-point rounding.
 */
fun CompletedChecklist.completionPercent(): Int {
    if (items.isEmpty()) return 0
    return items.count { it.completed } * 100 / items.size
}

// Control characters rather than normal punctuation, so they cannot collide with user-entered
// checklist/section/item names. Distinct from ChecklistDraftSaver's separators (different file
// scope) even though the char values happen to match.
private val FIELD_SEPARATOR: String = Char(1).toString()
private val RECORD_SEPARATOR: String = Char(2).toString()

/**
 * Encodes the saved-checklist list so it can survive recomposition/configuration changes via
 * `rememberSaveable` without adding a Parcelize dependency. Each checklist contributes exactly 4
 * consecutive strings (id, name, encoded sections, encoded items), so the flat list always decodes
 * unambiguously by chunking in groups of 4 regardless of empty names or empty trailing
 * sections/items blobs -- this is a saveable-state representation only, not durable persistence
 * (see docs/android/architecture.md "Data direction").
 */
val CompletedChecklistListSaver: Saver<List<CompletedChecklist>, ArrayList<String>> = Saver(
    save = { checklists ->
        val encoded = ArrayList<String>(checklists.size * 4)
        checklists.forEach { checklist ->
            encoded.add(checklist.id.toString())
            encoded.add(checklist.name)
            encoded.add(checklist.sections.joinToString(RECORD_SEPARATOR) { it.encode() })
            encoded.add(checklist.items.joinToString(RECORD_SEPARATOR) { it.encode() })
        }
        encoded
    },
    restore = { encoded ->
        encoded.chunked(4).map { (id, name, sectionsBlob, itemsBlob) ->
            CompletedChecklist(
                id = id.toLong(),
                name = name,
                sections = sectionsBlob.splitRecords().map { it.decodeSection() },
                items = itemsBlob.splitRecords().map { it.decodeItem() }
            )
        }
    }
)

private fun String.splitRecords(): List<String> = if (isEmpty()) emptyList() else split(RECORD_SEPARATOR)

private fun SavedChecklistSection.encode(): String = listOf(id.toString(), name).joinToString(FIELD_SEPARATOR)

private fun String.decodeSection(): SavedChecklistSection {
    val fields = split(FIELD_SEPARATOR)
    return SavedChecklistSection(id = fields[0].toLong(), name = fields[1])
}

private fun SavedChecklistItem.encode(): String = listOf(
    id.toString(),
    name,
    sectionId?.toString().orEmpty(),
    includeFile.toString(),
    completed.toString()
).joinToString(FIELD_SEPARATOR)

private fun String.decodeItem(): SavedChecklistItem {
    val fields = split(FIELD_SEPARATOR)
    return SavedChecklistItem(
        id = fields[0].toLong(),
        name = fields[1],
        sectionId = fields[2].ifEmpty { null }?.toLong(),
        includeFile = fields[3].toBoolean(),
        completed = fields[4].toBoolean()
    )
}
