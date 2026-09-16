package co.check2go.feature.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReminderPickerScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun setAReminderIsDisabledUntilBothDateAndTimeAreValid() {
        composeRule.setContent {
            Check2GoTheme {
                var date by remember { mutableStateOf("") }
                var time by remember { mutableStateOf("") }
                ReminderPickerScreen(
                    date = date,
                    onDateChange = { date = it },
                    time = time,
                    onTimeChange = { time = it },
                    onBack = {},
                    onConfirm = {}
                )
            }
        }

        composeRule.onNodeWithTag("reminder_set_action").assertIsNotEnabled()

        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-10-01")
        composeRule.onNodeWithTag("reminder_set_action").assertIsNotEnabled()

        composeRule.onNodeWithTag("reminder_time_field").performTextInput("09:30")
        composeRule.onNodeWithTag("reminder_set_action").assertIsEnabled()
    }

    @Test
    fun invalidDateOrTimeKeepsTheActionDisabledEvenWhenBothFieldsAreNonEmpty() {
        composeRule.setContent {
            Check2GoTheme {
                var date by remember { mutableStateOf("") }
                var time by remember { mutableStateOf("") }
                ReminderPickerScreen(
                    date = date,
                    onDateChange = { date = it },
                    time = time,
                    onTimeChange = { time = it },
                    onBack = {},
                    onConfirm = {}
                )
            }
        }

        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-13-40")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("09:30")

        composeRule.onNodeWithTag("reminder_set_action").assertIsNotEnabled()
    }

    @Test
    fun confirmingWithValidValuesEmitsOnConfirm() {
        var confirmed = false
        composeRule.setContent {
            Check2GoTheme {
                var date by remember { mutableStateOf("2026-10-01") }
                var time by remember { mutableStateOf("09:30") }
                ReminderPickerScreen(
                    date = date,
                    onDateChange = { date = it },
                    time = time,
                    onTimeChange = { time = it },
                    onBack = {},
                    onConfirm = { confirmed = true }
                )
            }
        }

        composeRule.onNodeWithTag("reminder_set_action").performClick()

        assertTrue(confirmed)
    }

    @Test
    fun backEmitsCallback() {
        var backRequested = false
        composeRule.setContent {
            Check2GoTheme {
                ReminderPickerScreen(
                    date = "",
                    onDateChange = {},
                    time = "",
                    onTimeChange = {},
                    onBack = { backRequested = true },
                    onConfirm = {}
                )
            }
        }

        composeRule.onNodeWithText("Back").performClick()

        assertTrue(backRequested)
    }

    @Test
    fun prefilledValuesAreDisplayedForEditingAnExistingReminder() {
        composeRule.setContent {
            Check2GoTheme {
                ReminderPickerScreen(
                    date = "2026-10-01",
                    onDateChange = {},
                    time = "09:30",
                    onTimeChange = {},
                    onBack = {},
                    onConfirm = {}
                )
            }
        }

        composeRule.onNodeWithText("2026-10-01").assertIsDisplayed()
        composeRule.onNodeWithText("09:30").assertIsDisplayed()
        composeRule.onNodeWithTag("reminder_set_action").assertIsEnabled()
    }

    @Test
    fun clearingAPrefilledFieldDisablesTheActionAgain() {
        composeRule.setContent {
            Check2GoTheme {
                var date by remember { mutableStateOf("2026-10-01") }
                var time by remember { mutableStateOf("09:30") }
                ReminderPickerScreen(
                    date = date,
                    onDateChange = { date = it },
                    time = time,
                    onTimeChange = { time = it },
                    onBack = {},
                    onConfirm = {}
                )
            }
        }

        composeRule.onNodeWithTag("reminder_date_field").performTextClearance()

        composeRule.onNodeWithTag("reminder_set_action").assertIsNotEnabled()
    }
}
