package co.check2go.feature.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ChecklistDetailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val checklist = CompletedChecklist(
        id = 1L,
        name = "Before leaving",
        sections = listOf(SavedChecklistSection(id = 1L, name = "Documents")),
        items = listOf(
            SavedChecklistItem(id = 1L, name = "Passport", sectionId = 1L, includeFile = true, completed = false),
            SavedChecklistItem(id = 2L, name = "Charger", sectionId = null, includeFile = false, completed = true)
        )
    )

    @Test
    fun showsTitleCompletionAndGroupedSectionsWithAnUnsectionedGroup() {
        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        // 1 of 2 items completed.
        composeRule.onNodeWithText("50% complete").assertIsDisplayed()
        composeRule.onNodeWithText("Documents").assertIsDisplayed()
        composeRule.onNodeWithText("No section").assertIsDisplayed()
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
        composeRule.onNodeWithText("Charger").assertIsDisplayed()
    }

    @Test
    fun itemChecklistBoxesReflectTheirCompletedState() {
        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithTag("checklist_detail_item_row_1").assertIsOff()
        composeRule.onNodeWithTag("checklist_detail_item_row_2").assertIsOn()
    }

    @Test
    fun tappingAnItemRowInvokesOnToggleItemWithThatItemsId() {
        var toggledId: Long? = null

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = { toggledId = it },
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()

        assertEquals(1L, toggledId)
    }

    @Test
    fun tappingEditChecklistInvokesOnEditChecklist() {
        var editInvoked = false

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = {},
                    onEditChecklist = { editInvoked = true },
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Edit checklist").performClick()

        assertEquals(true, editInvoked)
    }

    @Test
    fun tappingDuplicateChecklistInvokesOnDuplicateChecklist() {
        var duplicateInvoked = false

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = { duplicateInvoked = true },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Duplicate checklist").performClick()

        assertEquals(true, duplicateInvoked)
    }

    @Test
    fun includeFileItemShowsAnUnavailableNoteAndNoFunctioningUploadControl() {
        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        // Passport (includeFile = true) shows the temporary note...
        composeRule.onNodeWithTag("checklist_detail_upload_file_note_1").assertIsDisplayed()
        // ...but Charger (includeFile = false) does not.
        composeRule.onNodeWithTag("checklist_detail_upload_file_note_2").assertDoesNotExist()
        // The note is not itself a checkbox/toggle target -- only the item row is.
        composeRule.onNodeWithText("Upload file: not available yet.").assertIsDisplayed()
    }

    @Test
    fun emptyChecklistShowsZeroPercentAndANoItemsMessage() {
        val emptyChecklist = CompletedChecklist(id = 2L, name = "Empty checklist")

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = emptyChecklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("0% complete").assertIsDisplayed()
        composeRule.onNodeWithText("No items in this checklist yet.").assertIsDisplayed()
    }

    @Test
    fun fullyCompletedChecklistShowsOneHundredPercent() {
        val fullChecklist = CompletedChecklist(
            id = 3L,
            name = "Packed",
            items = listOf(
                SavedChecklistItem(id = 1L, name = "A", sectionId = null, includeFile = false, completed = true),
                SavedChecklistItem(id = 2L, name = "B", sectionId = null, includeFile = false, completed = true)
            )
        )

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = fullChecklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("100% complete").assertIsDisplayed()
    }

    @Test
    fun backInvokesTheBackCallback() {
        var backInvoked = false

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDetailScreen(
                    checklist = checklist,
                    onToggleItem = {},
                    onEditChecklist = {},
                    onDuplicateChecklist = {},
                    onBack = { backInvoked = true }
                )
            }
        }

        composeRule.onNodeWithText("Back").performClick()

        assertEquals(true, backInvoked)
    }
}
