package co.check2go.feature.checklists

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure-transformation tests for editing an existing section/item in place (docs/flows.md Flow 8,
 * step 3), as opposed to [ChecklistDraft.withSectionAdded]/[ChecklistDraft.withItemAdded]/etc,
 * which only ever add/remove. No Compose involved: see [CompletedChecklistTest]'s doc comment for
 * why this lives under androidTest rather than a JVM `src/test` source set.
 */
class ChecklistDraftTest {

    @Test
    fun withSectionRenamedChangesOnlyTheMatchingSectionsName() {
        val draft = ChecklistDraft(
            sections = listOf(
                ChecklistSection(id = 1L, name = "Documents"),
                ChecklistSection(id = 2L, name = "Electronics")
            )
        )

        val renamed = draft.withSectionRenamed(sectionId = 1L, name = "Paperwork")

        assertEquals("Paperwork", renamed.sections.first { it.id == 1L }.name)
        assertEquals("Electronics", renamed.sections.first { it.id == 2L }.name)
    }

    @Test
    fun withItemNameChangedKeepsTheItemsIdAndOtherFields() {
        val draft = ChecklistDraft(
            items = listOf(ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 5L, includeFile = true))
        )

        val renamed = draft.withItemNameChanged(itemId = 1L, name = "Passport (renewed)")

        val item = renamed.items.single()
        assertEquals(1L, item.id)
        assertEquals("Passport (renewed)", item.name)
        assertEquals(5L, item.sectionId)
        assertEquals(true, item.includeFile)
    }

    @Test
    fun withItemSectionChangedReassignsAnExistingItemToAnotherSection() {
        val draft = ChecklistDraft(items = listOf(ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 1L, includeFile = false)))

        val reassigned = draft.withItemSectionChanged(itemId = 1L, sectionId = 2L)

        assertEquals(2L, reassigned.items.single().sectionId)
    }

    @Test
    fun withItemSectionChangedCanClearAnItemsSection() {
        val draft = ChecklistDraft(items = listOf(ChecklistItemDraft(id = 1L, name = "Passport", sectionId = 1L, includeFile = false)))

        val cleared = draft.withItemSectionChanged(itemId = 1L, sectionId = null)

        assertEquals(null, cleared.items.single().sectionId)
    }

    @Test
    fun withItemIncludeFileChangedFlipsOnlyTheMatchingItemsFlag() {
        val draft = ChecklistDraft(
            items = listOf(
                ChecklistItemDraft(id = 1L, name = "Passport", sectionId = null, includeFile = false),
                ChecklistItemDraft(id = 2L, name = "Charger", sectionId = null, includeFile = false)
            )
        )

        val updated = draft.withItemIncludeFileChanged(itemId = 1L, includeFile = true)

        assertEquals(true, updated.items.first { it.id == 1L }.includeFile)
        assertEquals(false, updated.items.first { it.id == 2L }.includeFile)
    }
}
