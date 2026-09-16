package co.check2go.feature.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TripCreateDatesScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadTicketOpensApprovedSheet() {
        composeRule.setContent { Check2GoTheme { TripCreateDatesScreen(false, {}, "", {}, "", {}, false, null, {}, {}, {}, {}, {}) } }
        composeRule.onNodeWithText("Load your ticket").performClick()
        composeRule.onNodeWithText("Choose from gallery").assertIsDisplayed()
        composeRule.onNodeWithText("Recently downloaded:").assertIsDisplayed()
    }

    @Test
    fun departureCalendarSavesSelectedDate() {
        var departure = ""
        composeRule.setContent {
            Check2GoTheme {
                TripCreateDatesScreen(false, {}, departure, { departure = it }, "", {}, false, null, {}, {}, {}, {}, {})
            }
        }
        composeRule.onNodeWithTag("departure_date_picker").performClick()
        composeRule.onNodeWithText("Fri, 9 June").assertIsDisplayed()
        composeRule.onNodeWithTag("save_trip_date").performClick()
        composeRule.onNodeWithText("2024-06-09").assertIsDisplayed()
    }

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
                    reminderSummary = null,
                    onOpenReminderPicker = {},
                    onReminderDisabled = {},
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
                    reminderSummary = "Reminder: 2026-10-01 at 09:00",
                    onOpenReminderPicker = {},
                    onReminderDisabled = {},
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
                    reminderSummary = null,
                    onOpenReminderPicker = {},
                    onReminderDisabled = {},
                    onLoadTicket = {},
                    onBack = { backRequested = true },
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithText("Back").performClick()

        assertTrue(backRequested)
    }

    @Test
    fun turningReminderControlOnRequestsThePickerWithoutEnablingItDirectly() {
        var pickerRequested = false
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
                    reminderSummary = null,
                    onOpenReminderPicker = { pickerRequested = true },
                    onReminderDisabled = {},
                    onLoadTicket = {},
                    onBack = {},
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOff()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()

        assertTrue(pickerRequested)
        // The screen's own state (reminderEnabled) is unchanged here -- enabling only happens once
        // the caller confirms a reminder in the picker and passes reminderEnabled = true back down.
        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOff()
    }

    @Test
    fun turningReminderControlOffWhenEnabledDisablesItDirectlyWithoutOpeningThePicker() {
        var pickerRequested = false
        var disabled = false
        composeRule.setContent {
            Check2GoTheme {
                TripCreateDatesScreen(
                    oneWay = false,
                    onOneWayChange = {},
                    departureDate = "",
                    onDepartureDateChange = {},
                    returnDate = "",
                    onReturnDateChange = {},
                    reminderEnabled = true,
                    reminderSummary = "Reminder: 2026-10-01 at 09:00",
                    onOpenReminderPicker = { pickerRequested = true },
                    onReminderDisabled = { disabled = true },
                    onLoadTicket = {},
                    onBack = {},
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOn()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()

        assertTrue(disabled)
        assertFalse(pickerRequested)
    }

    @Test
    fun reminderSummaryAndEditActionAreOnlyShownWhenEnabled() {
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
                    reminderSummary = null,
                    onOpenReminderPicker = {},
                    onReminderDisabled = {},
                    onLoadTicket = {},
                    onBack = {},
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithText("Edit").assertDoesNotExist()
    }

    @Test
    fun editActionOnAnEnabledReminderRequestsThePicker() {
        var pickerRequested = false
        composeRule.setContent {
            Check2GoTheme {
                TripCreateDatesScreen(
                    oneWay = false,
                    onOneWayChange = {},
                    departureDate = "",
                    onDepartureDateChange = {},
                    returnDate = "",
                    onReturnDateChange = {},
                    reminderEnabled = true,
                    reminderSummary = "Reminder: 2026-10-01 at 09:00",
                    onOpenReminderPicker = { pickerRequested = true },
                    onReminderDisabled = {},
                    onLoadTicket = {},
                    onBack = {},
                    onNextStep = {}
                )
            }
        }

        composeRule.onNodeWithText("Reminder: 2026-10-01 at 09:00").assertIsDisplayed()
        composeRule.onNodeWithText("Edit").performClick()

        assertTrue(pickerRequested)
    }
}
