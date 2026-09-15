package co.check2go.feature.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ChecklistDuplicateConfirmationScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTheDuplicatedChecklistsNameAndBothActions() {
        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDuplicateConfirmationScreen(
                    checklistName = "Before leaving",
                    onGoBack = {},
                    onUseNow = {}
                )
            }
        }

        composeRule.onNodeWithText("A copy has been created").assertIsDisplayed()
        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        composeRule.onNodeWithText("Go back").assertIsDisplayed()
        composeRule.onNodeWithText("Use now").assertIsDisplayed()
    }

    @Test
    fun tappingGoBackInvokesOnGoBack() {
        var goBackInvoked = false

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDuplicateConfirmationScreen(
                    checklistName = "Before leaving",
                    onGoBack = { goBackInvoked = true },
                    onUseNow = {}
                )
            }
        }

        composeRule.onNodeWithTag("checklist_duplicate_go_back").performClick()

        assertEquals(true, goBackInvoked)
    }

    @Test
    fun tappingUseNowInvokesOnUseNow() {
        var useNowInvoked = false

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                ChecklistDuplicateConfirmationScreen(
                    checklistName = "Before leaving",
                    onGoBack = {},
                    onUseNow = { useNowInvoked = true }
                )
            }
        }

        composeRule.onNodeWithTag("checklist_duplicate_use_now").performClick()

        assertEquals(true, useNowInvoked)
    }
}
