package co.check2go.feature.checklists

import androidx.compose.runtime.saveable.Saver

/**
 * Smallest useful local summary of a checklist that finished CHECKLIST_CREATE (Flow 7 "Save
 * changes"), shown as a row on CHECKLISTS_POPULATED. Local-only: no Room/backend persistence, see
 * docs/android/architecture.md "Data direction".
 *
 * Context and completion are not stored: trip linking is out of scope for this task so every
 * checklist currently has the same neutral personal context, and item completion toggling is out
 * of scope so every checklist is always at 0% (docs/screen-inventory.md "Checklists — main tab").
 */
data class CompletedChecklist(val id: Long, val name: String)

// A control character rather than a normal punctuation mark, so it cannot collide with
// user-entered checklist names.
private val FIELD_SEPARATOR: String = Char(1).toString()

/**
 * Encodes each [CompletedChecklist] as a delimited string so the completed-checklist list can
 * survive recomposition/configuration changes via `rememberSaveable` without adding a Parcelize
 * dependency, mirroring `CompletedTripListSaver`.
 */
val CompletedChecklistListSaver: Saver<List<CompletedChecklist>, ArrayList<String>> = Saver(
    save = { checklists -> ArrayList(checklists.map { it.encode() }) },
    restore = { encoded -> encoded.map { it.decode() } }
)

private fun CompletedChecklist.encode(): String = listOf(id.toString(), name).joinToString(FIELD_SEPARATOR)

private fun String.decode(): CompletedChecklist {
    val fields = split(FIELD_SEPARATOR)
    return CompletedChecklist(id = fields[0].toLong(), name = fields[1])
}
