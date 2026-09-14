package co.check2go.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class Check2GoAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun fillDestinationAndStart() {
        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Destination country").performTextInput("Italy")
        composeRule.onNodeWithText("Departure country").performTextInput("Israel")
        composeRule.onNodeWithText("Trip name").performTextInput("Summer trip")
        composeRule.onNodeWithText("Start").performClick()
    }

    @Test
    fun addTripOpensDestinationAndBackReturnsHome() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripDatesNext = { _, _ -> })
            }
        }

        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Start your first journey!").assertIsDisplayed()
    }

    @Test
    fun startOnDestinationOpensDatesScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripDatesNext = { _, _ -> })
            }
        }

        fillDestinationAndStart()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithText("Quick Fill").assertIsDisplayed()
    }

    @Test
    fun backFromDatesPreservesDestinationDraft() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripDatesNext = { _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Italy").assertIsDisplayed()
        composeRule.onNodeWithText("Israel").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun nextStepEmitsDestinationAndDatesDrafts() {
        var destination: TripDestinationDraft? = null
        var dates: TripDatesDraft? = null
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(
                    onTripDatesNext = { d, dt ->
                        destination = d
                        dates = dt
                    }
                )
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithText("Return date").performTextInput("2026-10-10")
        composeRule.onNodeWithText("Next step").performClick()

        assertEquals(TripDestinationDraft("Italy", "Israel", "Summer trip"), destination)
        assertEquals(TripDatesDraft(false, "2026-10-01", "2026-10-10", false), dates)
    }
}
