package co.check2go.feature.checklists

import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private val fakeSaverScope = object : SaverScope {
    override fun canBeSaved(value: Any) = true
}

/**
 * Pure-transformation tests for the saved-checklist model: no Compose involved, so these run as
 * plain JUnit assertions (this module has no JVM `src/test` source set, see build.gradle.kts, so
 * they live alongside the other checklist tests under androidTest).
 */
class CompletedChecklistTest {

    @Test
    fun draftConversionCarriesSectionsAndItemsAndStartsEveryItemUncompleted() {
        val draft = ChecklistDraft(
            name = "Before leaving",
            sections = listOf(ChecklistSection(id = 1L, name = "Documents")),
            items = listOf(
                ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 1L, includeFile = true),
                ChecklistItemDraft(id = 2L, name = "Charger", sectionId = null, includeFile = false)
            )
        )

        val saved = draft.toSavedChecklist(id = 42L)

        assertEquals(42L, saved.id)
        assertEquals("Before leaving", saved.name)
        assertEquals(listOf(SavedChecklistSection(id = 1L, name = "Documents")), saved.sections)
        assertEquals(
            listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = false),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
            ),
            saved.items
        )
    }

    @Test
    fun draftConversionOfAnEmptyDraftProducesNoSectionsOrItems() {
        val saved = ChecklistDraft(name = "Empty").toSavedChecklist(id = 1L)

        assertTrue(saved.sections.isEmpty())
        assertTrue(saved.items.isEmpty())
    }

    @Test
    fun toDraftPrefillsNameSectionsAndItemsWithTheirOriginalIds() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = true),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
            )
        )

        val draft = checklist.toDraft()

        assertEquals("Before leaving", draft.name)
        assertEquals(listOf(ChecklistSection(id = 1L, name = "Documents")), draft.sections)
        assertEquals(
            listOf(
                ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 1L, includeFile = true),
                ChecklistItemDraft(id = 2L, name = "Charger", sectionId = null, includeFile = false)
            ),
            draft.items
        )
    }

    @Test
    fun withDraftAppliedPreservesCompletionForItemsThatKeptTheirId() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = null, includeFile = false, completed = true),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
            )
        )

        // Same ids as the original items, but the name of item 1 was edited -- still the "same"
        // item, so its completion must be preserved.
        val editedDraft = ChecklistDraft(
            name = "Before leaving",
            items = listOf(
                ChecklistItemDraft(id = 1L, name = "Passport (renamed)", sectionId = null, includeFile = false),
                ChecklistItemDraft(id = 2L, name = "Charger", sectionId = null, includeFile = false)
            )
        )

        val updated = checklist.withDraftApplied(editedDraft)

        assertEquals("Passport (renamed)", updated.items.first { it.id == 1L }.name)
        assertTrue(updated.items.first { it.id == 1L }.completed)
        assertFalse(updated.items.first { it.id == 2L }.completed)
    }

    @Test
    fun withDraftAppliedStartsNewlyAddedItemsUncompleted() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = null, includeFile = false, completed = true)
            )
        )

        val editedDraft = ChecklistDraft(
            name = "Before leaving",
            items = listOf(
                ChecklistItemDraft(id = 1L, name = "Passport", sectionId = null, includeFile = false),
                // A new item, added during the edit session with a fresh id never seen on this checklist.
                ChecklistItemDraft(id = 2L, name = "Boarding pass", sectionId = null, includeFile = true)
            )
        )

        val updated = checklist.withDraftApplied(editedDraft)

        assertTrue(updated.items.first { it.id == 1L }.completed)
        assertFalse(updated.items.first { it.id == 2L }.completed)
    }

    @Test
    fun withDraftAppliedDropsItemsRemovedFromTheDraft() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = null, includeFile = false, completed = true),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
            )
        )

        // Item 2 was removed in the editor.
        val editedDraft = ChecklistDraft(
            name = "Before leaving",
            items = listOf(ChecklistItemDraft(id = 1L, name = "Passport", sectionId = null, includeFile = false))
        )

        val updated = checklist.withDraftApplied(editedDraft)

        assertEquals(1, updated.items.size)
        assertEquals(1L, updated.items.first().id)
    }

    @Test
    fun withDraftAppliedSavesAnExistingItemsChangedSectionAndIncludeFile() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = null, includeFile = false, completed = true)
            )
        )

        // Same id as the original item, but reassigned into a section and marked to include a
        // file -- both edited in place in the editor, not via a remove+re-add.
        val editedDraft = ChecklistDraft(
            name = "Before leaving",
            sections = listOf(ChecklistSection(id = 1L, name = "Documents")),
            items = listOf(ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 1L, includeFile = true))
        )

        val updated = checklist.withDraftApplied(editedDraft)

        val savedItem = updated.items.single()
        assertEquals(1L, savedItem.sectionId)
        assertTrue(savedItem.includeFile)
        // Completion is preserved even though sectionId/includeFile changed.
        assertTrue(savedItem.completed)
    }

    @Test
    fun withDraftAppliedUpdatesNameAndSectionsAndKeepsTheChecklistId() {
        val checklist = CompletedChecklist(id = 7L, name = "Before leaving")

        val editedDraft = ChecklistDraft(
            name = "Before leaving (updated)",
            sections = listOf(ChecklistSection(id = 1L, name = "Documents"))
        )

        val updated = checklist.withDraftApplied(editedDraft)

        assertEquals(7L, updated.id)
        assertEquals("Before leaving (updated)", updated.name)
        assertEquals(listOf(SavedChecklistSection(id = 1L, name = "Documents")), updated.sections)
    }

    @Test
    fun togglingAnItemFlipsOnlyThatItemsCompletion() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = null, includeFile = false, completed = false),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
            )
        )

        val toggledOnce = checklist.withItemCompletionToggled(1L)
        assertTrue(toggledOnce.items.first { it.id == 1L }.completed)
        assertFalse(toggledOnce.items.first { it.id == 2L }.completed)

        val toggledTwice = toggledOnce.withItemCompletionToggled(1L)
        assertFalse(toggledTwice.items.first { it.id == 1L }.completed)
    }

    @Test
    fun emptyChecklistCompletionIsZeroPercent() {
        val checklist = CompletedChecklist(id = 1L, name = "Empty")

        assertEquals(0, checklist.completionPercent())
    }

    @Test
    fun partiallyCompletedChecklistRoundsDownWithoutFloatingPointDrift() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Packing",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "A", sectionId = null, includeFile = false, completed = true),
                SavedChecklistItem(id = 2L, name = "B", sectionId = null, includeFile = false, completed = false),
                SavedChecklistItem(id = 3L, name = "C", sectionId = null, includeFile = false, completed = false)
            )
        )

        // 1 of 3 = 33.33...%, must deterministically floor to 33, not drift to 32 or 34.
        assertEquals(33, checklist.completionPercent())
    }

    @Test
    fun fullyCompletedChecklistIsOneHundredPercent() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Packing",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "A", sectionId = null, includeFile = false, completed = true),
                SavedChecklistItem(id = 2L, name = "B", sectionId = null, includeFile = false, completed = true)
            )
        )

        assertEquals(100, checklist.completionPercent())
    }

    @Test
    fun duplicateAssignsTheGivenIdAndPreservesTheNameAndStructure() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = true),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
            )
        )
        var nextSectionId = 50L
        var nextItemId = 100L

        val duplicated = checklist.duplicate(
            id = 99L,
            nextSectionId = { nextSectionId++ },
            nextItemId = { nextItemId++ }
        )

        assertEquals(99L, duplicated.id)
        assertEquals("Before leaving", duplicated.name)
        assertEquals(1, duplicated.sections.size)
        assertEquals("Documents", duplicated.sections.single().name)
        assertEquals(2, duplicated.items.size)
        assertEquals("Passport", duplicated.items[0].name)
        assertEquals(true, duplicated.items[0].includeFile)
        assertEquals("Charger", duplicated.items[1].name)
        assertEquals(false, duplicated.items[1].includeFile)
    }

    @Test
    fun duplicateResetsEveryCopiedItemsCompletionToFalseRegardlessOfTheSourcesState() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Packing",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "A", sectionId = null, includeFile = false, completed = true),
                SavedChecklistItem(id = 2L, name = "B", sectionId = null, includeFile = false, completed = false)
            )
        )

        val duplicated = checklist.duplicate(id = 2L, nextSectionId = { 1L }, nextItemId = { 1L })

        assertTrue(duplicated.items.none { it.completed })
    }

    @Test
    fun duplicateAssignsFreshSectionAndItemIdsRatherThanReusingTheSources() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
            items = listOf(SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = false, completed = false))
        )
        var nextSectionId = 10L
        var nextItemId = 20L

        val duplicated = checklist.duplicate(
            id = 2L,
            nextSectionId = { nextSectionId++ },
            nextItemId = { nextItemId++ }
        )

        // Ids come from the injected generators, not the source checklist's own ids -- so a
        // duplicate can never collide with the source or with anything assigned after it.
        assertEquals(10L, duplicated.sections.single().id)
        assertEquals(20L, duplicated.items.single().id)
    }

    @Test
    fun duplicateRemapsItemSectionAssignmentsOntoTheCopiedSectionIds() {
        val checklist = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            sections = listOf(
                SavedChecklistSection(id = 1L, name = "Documents"),
                SavedChecklistSection(id = 2L, name = "Electronics")
            ),
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = false, completed = false),
                SavedChecklistItem(id = 2L, name = "Charger", sectionId = 2L, includeFile = false, completed = false),
                SavedChecklistItem(id = 3L, name = "Notebook", sectionId = null, includeFile = false, completed = false)
            )
        )
        var nextSectionId = 100L
        var nextItemId = 200L

        val duplicated = checklist.duplicate(
            id = 2L,
            nextSectionId = { nextSectionId++ },
            nextItemId = { nextItemId++ }
        )

        val documentsCopyId = duplicated.sections.first { it.name == "Documents" }.id
        val electronicsCopyId = duplicated.sections.first { it.name == "Electronics" }.id
        assertEquals(documentsCopyId, duplicated.items.first { it.name == "Passport" }.sectionId)
        assertEquals(electronicsCopyId, duplicated.items.first { it.name == "Charger" }.sectionId)
        assertEquals(null, duplicated.items.first { it.name == "Notebook" }.sectionId)
    }

    @Test
    fun duplicateDoesNotMutateTheSourceChecklist() {
        val original = CompletedChecklist(
            id = 1L,
            name = "Before leaving",
            sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
            items = listOf(
                SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = true)
            )
        )
        val snapshot = original.copy()

        original.duplicate(id = 2L, nextSectionId = { 1L }, nextItemId = { 1L })

        assertEquals(snapshot, original)
    }

    @Test
    fun listSaverRoundTripsMultipleChecklistsIncludingEmptyNamesAndUnsectionedItems() {
        val checklists = listOf(
            CompletedChecklist(
                id = 1L,
                name = "Before leaving",
                sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
                items = listOf(
                    SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = true),
                    SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = false)
                )
            ),
            // Empty name and no sections/items at all: exercises the last-field/empty-blob edges.
            CompletedChecklist(id = 2L, name = "")
        )

        val encoded = with(CompletedChecklistListSaver) { fakeSaverScope.save(checklists) }
        val restored = CompletedChecklistListSaver.restore(requireNotNull(encoded))

        assertEquals(checklists, restored)
    }

    @Test
    fun listSaverRoundTripsAnEmptyList() {
        val encoded = with(CompletedChecklistListSaver) { fakeSaverScope.save(emptyList()) }
        val restored = CompletedChecklistListSaver.restore(requireNotNull(encoded))

        assertTrue(restored!!.isEmpty())
    }
}
