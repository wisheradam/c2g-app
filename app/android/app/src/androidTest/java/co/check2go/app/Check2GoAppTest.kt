package co.check2go.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class Check2GoAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun addTripOpensDestinationAndBackReturnsHome() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripStart = {})
            }
        }

        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Start your first journey!").assertIsDisplayed()
    }

    @Test
    fun formEntryAndStartEmitDraft() {
        var submitted: TripDestinationDraft? = null
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripStart = { submitted = it })
            }
        }

        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Destination country").performTextInput("Italy")
        composeRule.onNodeWithText("Departure country").performTextInput("Israel")
        composeRule.onNodeWithText("Trip name").performTextInput("Summer trip")
        composeRule.onNodeWithText("Start").performClick()

        assertEquals(
            TripDestinationDraft("Italy", "Israel", "Summer trip"),
            submitted
        )
    }
}
