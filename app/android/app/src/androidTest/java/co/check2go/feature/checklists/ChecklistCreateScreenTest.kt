package co.check2go.feature.checklists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Drives the real, stateless [ChecklistCreateScreen] through a minimal stateful harness that
 * mirrors how `Check2GoApp` wires it up, so these tests exercise the actual draft transformations
 * (`withSectionAdded`, `withItemAdded`, ...) rather than a test-only stand-in.
 */
private class HarnessResult(
    var savedDraft: ChecklistDraft? = null,
    var backInvoked: Boolean = false
)

@Composable
private fun ChecklistCreateHarness(result: HarnessResult, initialDraft: ChecklistDraft = ChecklistDraft()) {
    var draft by remember { mutableStateOf(initialDraft) }
    var nextSectionId by remember { mutableStateOf(1L) }
    var nextItemId by remember { mutableStateOf(1L) }
    var newSectionName by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }
    var newItemSectionId by remember { mutableStateOf<Long?>(null) }
    var newItemIncludeFile by remember { mutableStateOf(false) }

    ChecklistCreateScreen(
        draft = draft,
        onNameChange = { draft = draft.copy(name = it) },
        newSectionName = newSectionName,
        onNewSectionNameChange = { newSectionName = it },
        onAddSection = {
            if (newSectionName.isNotBlank()) {
                draft = draft.withSectionAdded(ChecklistSection(id = nextSectionId, name = newSectionName.trim()))
                nextSectionId += 1
                newSectionName = ""
            }
        },
        onSectionNameChange = { sectionId, name -> draft = draft.withSectionRenamed(sectionId, name) },
        onRemoveSection = { sectionId ->
            draft = draft.withSectionRemoved(sectionId)
            if (newItemSectionId == sectionId) newItemSectionId = null
        },
        newItemName = newItemName,
        onNewItemNameChange = { newItemName = it },
        newItemSectionId = newItemSectionId,
        onNewItemSectionIdChange = { newItemSectionId = it },
        newItemIncludeFile = newItemIncludeFile,
        onNewItemIncludeFileChange = { newItemIncludeFile = it },
        onAddItem = {
            if (newItemName.isNotBlank()) {
                draft = draft.withItemAdded(
                    ChecklistItemDraft(
                        id = nextItemId,
                        name = newItemName.trim(),
                        sectionId = newItemSectionId,
                        includeFile = newItemIncludeFile
                    )
                )
                nextItemId += 1
                newItemName = ""
                newItemSectionId = null
                newItemIncludeFile = false
            }
        },
        onItemNameChange = { itemId, name -> draft = draft.withItemNameChanged(itemId, name) },
        onItemSectionChange = { itemId, sectionId -> draft = draft.withItemSectionChanged(itemId, sectionId) },
        onItemIncludeFileChange = { itemId, includeFile ->
            draft = draft.withItemIncludeFileChanged(itemId, includeFile)
        },
        onRemoveItem = { itemId -> draft = draft.withItemRemoved(itemId) },
        onBack = { result.backInvoked = true },
        onSave = { result.savedDraft = draft }
    )
}

class ChecklistCreateScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun enteringNameUpdatesTheDraftShownOnScreen() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")

        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
    }

    @Test
    fun addingAndRemovingASectionUpdatesTheSectionsList() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()

        composeRule.onNodeWithTag("checklist_section_row_1").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Remove section Documents").performClick()

        composeRule.onNodeWithTag("checklist_section_row_1").assertDoesNotExist()
    }

    @Test
    fun addingAnItemWithNoSectionAndFileToggleOffAppendsAnItem() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        composeRule.onNodeWithTag("checklist_item_row_1").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_item_section_field_1").assertIsDisplayed()
    }

    @Test
    fun selectingAnExistingSectionForANewItemAssociatesIt() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()

        composeRule.onNodeWithTag("new_item_section_field").performClick()
        composeRule.onNodeWithTag("new_item_section_field_option_1").performClick()

        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        composeRule.onNodeWithTag("checklist_item_section_field_1").assertTextContains("Documents", substring = true)
    }

    @Test
    fun toggleIncludeFileMarksTheAddedItem() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithTag("new_item_include_file_toggle").assertIsOff()
        composeRule.onNodeWithTag("new_item_include_file_toggle").performClick()
        composeRule.onNodeWithTag("new_item_include_file_toggle").assertIsOn()

        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        composeRule.onNodeWithTag("checklist_item_include_file_toggle_1").assertIsOn()
        // The toggle resets for the next new-item entry.
        composeRule.onNodeWithTag("new_item_include_file_toggle").assertIsOff()
    }

    @Test
    fun renamingAnExistingSectionUpdatesItsNameInTheSavedDraft() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()

        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextClearance()
        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextInput("Paperwork")
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        assertEquals("Paperwork", result.savedDraft?.sections?.first { it.id == 1L }?.name)
    }

    @Test
    fun editingAnExistingItemsNameSectionAndIncludeFileUpdatesTheSavedDraft() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()
        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        // Edit the existing item in place: rename it, move it into the section, and turn on
        // include-file -- none of this goes through onAddItem, which only ever appends a new item.
        composeRule.onNodeWithTag("checklist_item_name_field_1").performTextClearance()
        composeRule.onNodeWithTag("checklist_item_name_field_1").performTextInput("Passport (renewed)")
        composeRule.onNodeWithTag("checklist_item_section_field_1").performClick()
        composeRule.onNodeWithTag("checklist_item_section_field_1_option_1").performClick()
        composeRule.onNodeWithTag("checklist_item_include_file_toggle_1")
            .performScrollTo()
            .performClick()
            .assertIsOn()

        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        val savedItem = result.savedDraft?.items?.first { it.id == 1L }
        assertEquals("Passport (renewed)", savedItem?.name)
        assertEquals(1L, savedItem?.sectionId)
        assertEquals(true, savedItem?.includeFile)
        // Editing in place keeps the item's id stable and does not append a second item.
        assertEquals(1, result.savedDraft?.items?.size)
    }

    @Test
    fun removingAPreviouslyAddedItemRemovesItFromTheList() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithTag("checklist_item_row_1").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Remove item Passport").performClick()

        composeRule.onNodeWithTag("checklist_item_row_1").assertDoesNotExist()
    }

    @Test
    fun saveIsDisabledUntilNameIsEntered() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()
        assertNull(result.savedDraft)

        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Save changes").performClick()

        assertEquals("Before leaving", result.savedDraft?.name)
    }

    @Test
    fun saveIsDisabledWhenAnExistingSectionOrItemNameIsBlank() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()
        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextClearance()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()
        assertNull(result.savedDraft)

        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextInput("Documents")
        composeRule.onNodeWithTag("checklist_item_name_field_1").performTextClearance()
        composeRule.onNodeWithText("Save changes").performClick()
        assertNull(result.savedDraft)

        composeRule.onNodeWithTag("checklist_item_name_field_1").performTextInput("Passport")
        composeRule.onNodeWithText("Save changes").performClick()
        assertEquals("Before leaving", result.savedDraft?.name)
    }

    @Test
    fun saveCallbackReceivesTheFullStructuredDraft() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()
        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Include file").performClick()
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        val saved = result.savedDraft
        assertEquals("Before leaving", saved?.name)
        assertEquals(1, saved?.sections?.size)
        assertEquals("Documents", saved?.sections?.first()?.name)
        assertEquals(1, saved?.items?.size)
        val item = saved?.items?.first()
        assertEquals("Passport", item?.name)
        assertEquals(true, item?.includeFile)
    }

    @Test
    fun backInvokesTheBackCallback() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Back").performClick()

        assertEquals(true, result.backInvoked)
    }

    @Test
    fun duplicateChecklistActionIsHiddenWhenOnDuplicateChecklistIsNull() {
        composeRule.setContent {
            Check2GoTheme {
                ChecklistCreateScreen(
                    draft = ChecklistDraft(),
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
                    onSave = {},
                    onDuplicateChecklist = null
                )
            }
        }

        composeRule.onNodeWithText("Duplicate checklist").assertDoesNotExist()
    }

    @Test
    fun duplicateChecklistActionInvokesOnDuplicateChecklistWhenProvided() {
        var duplicateInvoked = false

        composeRule.setContent {
            Check2GoTheme {
                ChecklistCreateScreen(
                    draft = ChecklistDraft(),
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
                    onSave = {},
                    onDuplicateChecklist = { duplicateInvoked = true }
                )
            }
        }

        composeRule.onNodeWithText("Duplicate checklist").performClick()

        assertEquals(true, duplicateInvoked)
    }
}
