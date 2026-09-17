package co.check2go.feature.trip

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TravelerManagementScreensTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun emptyStateAddInvokesCallback() {
        var added = false
        composeRule.setContent { Check2GoTheme { MyTravelersScreen(null, {}, { added = true }, {}) } }
        composeRule.onNodeWithText("Your travelers list is empty").assertIsDisplayed()
        composeRule.onNodeWithText("Add a new traveler", substring = true).performClick()
        assertEquals(true, added)
    }

    @Test fun editorSavesEnteredName() {
        var saved = ""
        composeRule.setContent { Check2GoTheme { TravelerEditorScreen("", {}, { saved = it }) } }
        composeRule.onNodeWithTag("traveler_name").performTextInput("Alex")
        composeRule.onNodeWithText("Save changes").performClick()
        assertEquals("Alex", saved)
    }

    @Test fun replacePhotoOpensApprovedSheet() {
        composeRule.setContent { Check2GoTheme { TravelerEditorScreen("Alex", {}, {}) } }
        composeRule.onNodeWithTag("replace_photo").performClick()
        composeRule.onNodeWithText("Choose from gallery").assertIsDisplayed()
        composeRule.onNodeWithText("Make a photo").assertIsDisplayed()
        composeRule.onNodeWithText("Delete photo").assertIsDisplayed()
    }
}
