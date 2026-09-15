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
