package co.check2go.feature.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.core.design.AppDestination
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ChecklistsEmptyScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsVerifiedContentWithChecklistsTabSelected() {
        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistsEmptyScreen(
                    onCreateChecklist = {},
                    onQuickAdd = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("No active checklists").assertIsDisplayed()
        composeRule.onNodeWithText("Create checklist").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_home").assertIsDisplayed().assertIsNotSelected()
        composeRule.onNodeWithTag("nav_documents").assertIsDisplayed().assertIsNotSelected()
        composeRule.onNodeWithTag("nav_checklists").assertIsDisplayed().assertIsSelected()
        composeRule.onNodeWithTag("nav_events").assertIsDisplayed().assertIsNotSelected()
    }

    @Test
    fun createChecklistAndQuickAddReachTheSameCallback() {
        var invocationCount = 0
        val onCreateChecklist: () -> Unit = { invocationCount++ }

        composeRule.setContent {
            Check2GoTheme {
                ChecklistsEmptyScreen(
                    onCreateChecklist = onCreateChecklist,
                    onQuickAdd = onCreateChecklist,
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithContentDescription("Quick add").performClick()

        assertEquals(2, invocationCount)
    }

    @Test
    fun tappingHomeTabEmitsHomeDestination() {
        var selected: AppDestination? = null

        composeRule.setContent {
            Check2GoTheme {
                ChecklistsEmptyScreen(
                    onCreateChecklist = {},
                    onQuickAdd = {},
                    onDestinationSelected = { selected = it }
                )
            }
        }

        composeRule.onNodeWithText("Home").performClick()

        assertEquals(AppDestination.Home, selected)
    }
}
