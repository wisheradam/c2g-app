package co.check2go.feature.checklists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
        composeRule.onNodeWithTag("checklist_item_section_label_1").assertIsDisplayed()
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
        // The section row's own label and the dropdown menu item share the text "Documents";
        // only the menu item is clickable, so scope the click to that.
        composeRule.onNode(hasText("Documents") and hasClickAction()).performClick()

        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        composeRule.onNodeWithTag("checklist_item_section_label_1").assertIsDisplayed()
    }

    @Test
    fun toggleIncludeFileMarksTheAddedItem() {
        val result = HarnessResult()
        composeRule.setContent {
            Check2GoTheme { ChecklistCreateHarness(result) }
        }

        composeRule.onNodeWithText("Include file").assertIsOff()
        composeRule.onNodeWithText("Include file").performClick()
        composeRule.onNodeWithText("Include file").assertIsOn()

        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()

        composeRule.onNodeWithText("Includes a file").assertIsDisplayed()
        // The toggle resets for the next new-item entry.
        composeRule.onNodeWithText("Include file").assertIsOff()
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
}
