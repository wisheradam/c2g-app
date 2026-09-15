package co.check2go.feature.checklists

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ChecklistsPopulatedScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val sampleChecklists = listOf(
        CompletedChecklist(id = 1L, name = "Before leaving"),
        CompletedChecklist(id = 2L, name = "Documents")
    )

    @Test
    fun showsEachChecklistNameNeutralContextAndZeroPercentComplete() {
        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistsPopulatedScreen(
                    checklists = sampleChecklists,
                    onCreateChecklist = {},
                    onQuickAdd = {},
                    onChecklistSelected = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_card_2").assertIsDisplayed()
        composeRule.onAllNodesWithText("Personal checklist").assertCountEquals(2)
        composeRule.onAllNodesWithText("0% complete").assertCountEquals(2)
        composeRule.onNodeWithTag("nav_checklists").assertIsDisplayed().assertIsSelected()
        composeRule.onNodeWithTag("nav_home").assertIsDisplayed().assertIsNotSelected()
    }

    @Test
    fun cardShowsItsOwnCalculatedCompletionPercentage() {
        val checklists = listOf(
            CompletedChecklist(
                id = 1L,
                name = "Packing",
                items = listOf(
                    SavedChecklistItem(id = 1L, name = "A", sectionId = null, includeFile = false, completed = true),
                    SavedChecklistItem(id = 2L, name = "B", sectionId = null, includeFile = false, completed = false)
                )
            )
        )

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistsPopulatedScreen(
                    checklists = checklists,
                    onCreateChecklist = {},
                    onQuickAdd = {},
                    onChecklistSelected = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("50% complete").assertIsDisplayed()
    }

    @Test
    fun createChecklistAndQuickAddReachTheirCallbacks() {
        var createCount = 0
        var quickAddCount = 0

        composeRule.setContent {
            Check2GoTheme {
                ChecklistsPopulatedScreen(
                    checklists = sampleChecklists,
                    onCreateChecklist = { createCount++ },
                    onQuickAdd = { quickAddCount++ },
                    onChecklistSelected = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithContentDescription("Quick add").performClick()

        assertEquals(1, createCount)
        assertEquals(1, quickAddCount)
    }

    @Test
    fun tappingARowInvokesOnChecklistSelectedWithThatChecklistsId() {
        var selectedId: Long? = null

        composeRule.setContent {
            Check2GoTheme {
                ChecklistsPopulatedScreen(
                    checklists = sampleChecklists,
                    onCreateChecklist = {},
                    onQuickAdd = {},
                    onChecklistSelected = { selectedId = it },
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithTag("checklist_card_2").performClick()

        assertEquals(2L, selectedId)
    }
}
