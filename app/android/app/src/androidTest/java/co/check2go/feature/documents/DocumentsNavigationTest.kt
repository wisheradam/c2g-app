package co.check2go.feature.documents

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.check2go.app.Check2GoApp
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Rule
import org.junit.Test

class DocumentsNavigationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun documentsBottomTabOpensWallet() {
        compose.setContent { Check2GoTheme { Check2GoApp(onTripCreateComplete = { _, _, _ -> }) } }
        compose.onNodeWithTag("nav_documents").performClick()
        compose.onNodeWithText("Store and manage your travel documents in one place.").assertIsDisplayed()
        compose.onNodeWithTag("documents_search").assertIsDisplayed()
    }
}
