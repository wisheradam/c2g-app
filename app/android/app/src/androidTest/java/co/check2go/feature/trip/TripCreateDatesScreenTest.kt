package co.check2go.feature.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TripCreateDatesScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun returnDateVisibleByDefaultAndHiddenWhenOneWay() {
        composeRule.setContent {
            Check2GoTheme {
                var oneWay by remember { mutableStateOf(false) }
                TripCreateDatesScreen(
                    oneWay = oneWay,
                    onOneWayChange = { oneWay = it },
                    departureDate = "",
                    onDepartureDateChange = {},
                    returnDate = "",
                    onReturnDateChange = {},
                    reminderEnabled = false,
                    onReminderEnabledChange = {},
                    onLoadTicket = {},
                    onBack = {},
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithText("Return date").assertIsDisplayed()

        composeRule.onNodeWithText("Yes").performClick()
        composeRule.onNodeWithText("Return date").assertDoesNotExist()

        composeRule.onNodeWithText("No").performClick()
        composeRule.onNodeWithText("Return date").assertIsDisplayed()
    }

    @Test
    fun loadTicketAndNextStepEmitCallbacks() {
        var ticketRequested = false
        var nextStepRequested = false
        composeRule.setContent {
            Check2GoTheme {
                TripCreateDatesScreen(
                    oneWay = true,
                    onOneWayChange = {},
                    departureDate = "2026-10-01",
                    onDepartureDateChange = {},
                    returnDate = "",
                    onReturnDateChange = {},
                    reminderEnabled = true,
                    onReminderEnabledChange = {},
                    onLoadTicket = { ticketRequested = true },
                    onBack = {},
                    onNextStep = { nextStepRequested = true }
                )
            }
        }

        composeRule.onNodeWithText("Load your ticket").performClick()
        composeRule.onNodeWithText("Next step").performClick()

        assertTrue(ticketRequested)
        assertTrue(nextStepRequested)
    }

    @Test
    fun backEmitsCallback() {
        var backRequested = false
        composeRule.setContent {
            Check2GoTheme {
                TripCreateDatesScreen(
                    oneWay = false,
                    onOneWayChange = {},
                    departureDate = "",
                    onDepartureDateChange = {},
                    returnDate = "",
                    onReturnDateChange = {},
                    reminderEnabled = false,
                    onReminderEnabledChange = {},
                    onLoadTicket = {},
                    onBack = { backRequested = true },
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithText("Back").performClick()

        assertTrue(backRequested)
    }
}
