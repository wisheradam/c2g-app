package co.check2go.feature.trip

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TripHubScreenTest {
    @get:Rule val composeRule = createComposeRule()
    private val trip = CompletedTrip(1, "London - Tel Aviv", "Tel Aviv", "London", false, "25.03.2024", "06.04.2024")

    @Test fun showsApprovedHubSections() {
        composeRule.setContent { Check2GoTheme { TripHubScreen(trip, {}, {}) } }
        composeRule.onNodeWithText("Checklists").assertIsDisplayed()
        composeRule.onNodeWithText("Events Calendar").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Keep your tickets always at hand").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("What to see").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Request for car rental").performScrollTo().assertIsDisplayed()
    }

    @Test fun actionsEmitCallbacks() {
        var back = false
        var create = false
        composeRule.setContent { Check2GoTheme { TripHubScreen(trip, { back = true }, { create = true }) } }
        composeRule.onNodeWithText("Create new one").performClick()
        assertTrue(create)
        composeRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(back)
    }
}
