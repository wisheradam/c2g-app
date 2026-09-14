package co.check2go.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import co.check2go.feature.trip.TripAdventureType
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.feature.trip.TripTravelersDraft
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

    private fun fillDestinationDatesAndAdvanceToTravelers() {
        fillDestinationAndStart()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithText("Return date").performTextInput("2026-10-10")
        composeRule.onNodeWithText("Next step").performClick()
    }

    @Test
    fun addTripOpensDestinationAndBackReturnsHome() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
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
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
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
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
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
    fun nextStepFromDatesOpensTravelersScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()

        composeRule.onNodeWithText("Solo adventure").assertIsDisplayed()
        composeRule.onNodeWithText("Group adventure").assertIsDisplayed()
        composeRule.onNodeWithText("Complete").assertIsDisplayed()
    }

    @Test
    fun backFromTravelersPreservesDestinationAndDatesDrafts() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Group adventure").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-01").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-10").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Italy").assertIsDisplayed()
        composeRule.onNodeWithText("Israel").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun travelersStatePreservedWhenReturningFromDates() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Group adventure").performClick()
        composeRule.onNodeWithText("My travelers").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Next step").performClick()

        composeRule.onNodeWithText("My travelers").assertIsDisplayed()
    }

    @Test
    fun completeEmitsDestinationDatesAndTravelersDrafts() {
        var destination: TripDestinationDraft? = null
        var dates: TripDatesDraft? = null
        var travelers: TripTravelersDraft? = null
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(
                    onTripCreateComplete = { d, dt, tr ->
                        destination = d
                        dates = dt
                        travelers = tr
                    }
                )
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Group adventure").performClick()
        composeRule.onNodeWithText("Yes").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        assertEquals(TripDestinationDraft("Italy", "Israel", "Summer trip"), destination)
        assertEquals(TripDatesDraft(false, "2026-10-01", "2026-10-10", false), dates)
        assertEquals(TripTravelersDraft(TripAdventureType.Group, true), travelers)
    }

    @Test
    fun completingTripNavigatesToMyTripsWithVisibleSummary() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("My trips").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
        composeRule.onNodeWithText("Israel → Italy").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-01 – 2026-10-10").assertIsDisplayed()
    }

    @Test
    fun bothActiveAndAllFiltersShowTheCompletedTrip() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("All").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("Active").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun startingAnotherTripFromMyTripsResetsDraftAndKeepsCompletedTrip() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("Add a trip").performClick()

        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Italy").assertDoesNotExist()
        composeRule.onNodeWithText("Israel").assertDoesNotExist()
        composeRule.onNodeWithText("Summer trip").assertDoesNotExist()

        composeRule.onNodeWithText("Destination country").performTextInput("Norway")
        composeRule.onNodeWithText("Departure country").performTextInput("Spain")
        composeRule.onNodeWithText("Trip name").performTextInput("Winter escape")
        composeRule.onNodeWithText("Start").performClick()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-12-05")
        composeRule.onNodeWithText("Return date").performTextInput("2026-12-12")
        composeRule.onNodeWithText("Next step").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
        composeRule.onNodeWithText("Winter escape").assertIsDisplayed()
    }
}
