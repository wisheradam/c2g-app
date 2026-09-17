package co.check2go.feature.checklists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ShareChecklistScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun showsApprovedSubscriptionCopy() {
        composeRule.setContent { Check2GoTheme { ShareChecklistScreen({}, {}) } }
        composeRule.onNodeWithText("Share checklist").assertIsDisplayed()
        composeRule.onNodeWithText("Find out more").assertIsDisplayed()
        composeRule.onNodeWithText("You also have access to a 7 days free trial period").assertIsDisplayed()
    }

    @Test fun actionsEmitCallbacks() {
        var back = false
        var more = false
        composeRule.setContent { Check2GoTheme { ShareChecklistScreen({ back = true }, { more = true }) } }
        composeRule.onNodeWithTag("share_find_out_more").performClick()
        assertTrue(more)
        composeRule.onNodeWithTag("share_scrim").performClick()
        assertTrue(back)
    }
}
