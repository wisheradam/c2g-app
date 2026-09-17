package co.check2go.feature.events

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EventCalendarScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun approvedCalendarContentIsVisible() {
        composeRule.setContent {
            Check2GoTheme { EventCalendarScreen(onBack = {}, onSave = {}) }
        }

        composeRule.onNodeWithText("Select events dates").assertIsDisplayed()
        composeRule.onNodeWithText("June 2024").assertIsDisplayed()
        composeRule.onNodeWithText("Fri, 9 June").assertIsDisplayed()
        composeRule.onNodeWithText("Save date").assertIsDisplayed()
    }

    @Test
    fun saveReturnsSelectedDate() {
        var saved = ""
        composeRule.setContent {
            Check2GoTheme { EventCalendarScreen(onBack = {}, onSave = { saved = it }) }
        }

        composeRule.onNodeWithTag("events_save_date").performClick()

        assertEquals("2024-06-09", saved)
    }

    @Test
    fun tappingScrimReturnsBack() {
        var back = false
        composeRule.setContent {
            Check2GoTheme { EventCalendarScreen(onBack = { back = true }, onSave = {}) }
        }

        composeRule.onNodeWithTag("events_calendar_scrim").performClick()

        assertTrue(back)
    }
}
