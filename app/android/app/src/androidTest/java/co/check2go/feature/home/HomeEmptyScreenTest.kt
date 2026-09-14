package co.check2go.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeEmptyScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsVerifiedContentAndEmitsAddTripAction() {
        var addTripClicked = false

        composeRule.setContent {
            Check2GoTheme(darkTheme = false) {
                HomeEmptyScreen(
                    onAddTrip = { addTripClicked = true },
                    onQuickAdd = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Start your first journey!").assertIsDisplayed()
        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Documents").assertIsDisplayed()
        composeRule.onNodeWithText("Checklists").assertIsDisplayed()
        composeRule.onNodeWithText("Events").assertIsDisplayed()
        composeRule.onNodeWithText("Add a trip").performClick()

        assertTrue(addTripClicked)
    }
}
