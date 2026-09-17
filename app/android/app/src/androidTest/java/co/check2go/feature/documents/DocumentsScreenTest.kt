package co.check2go.feature.documents

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import co.check2go.ui.theme.Check2GoTheme
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DocumentsScreenTest {
    @get:Rule val compose = createComposeRule()
    private val documents = listOf(
        doc("p1", "Israeli Passport", DocumentCategory.Identity, "Passport", "Israel"),
        doc("p2", "Polish Passport", DocumentCategory.Identity, "Passport", "Poland"),
        doc("t1", "Tokyo Flight", DocumentCategory.Transportation, "Flight Ticket", "Japan")
    )

    @Test fun categoriesCollapseExpandAndSearchAutoExpandsMatches() {
        compose.setContent { Check2GoTheme { screen() } }
        compose.onNodeWithText("Israeli Passport").assertDoesNotExist()
        compose.onNodeWithTag("category_Identity").performClick()
        compose.onNodeWithText("Israeli Passport").assertIsDisplayed()
        compose.onNodeWithTag("category_Identity").performClick()
        compose.onNodeWithText("Israeli Passport").assertDoesNotExist()

        compose.onNodeWithTag("documents_search").performTextInput("tokyo")
        compose.onNodeWithText("Tokyo Flight").assertIsDisplayed()
        compose.onNodeWithText("Israeli Passport").assertDoesNotExist()
    }

    @Test fun addEditAndDeleteActionsAreConnected() {
        var addCalls = 0
        var edited: TravelDocument? = null
        var deleted: TravelDocument? = null
        compose.setContent {
            Check2GoTheme {
                DocumentsScreen(documents, { _, _ -> addCalls++ }, { edited = it }, { deleted = it }, {}, {})
            }
        }
        compose.onNodeWithTag("documents_add").performClick()
        assertEquals(1, addCalls)
        compose.onNodeWithTag("category_Identity").performClick()
        compose.onNodeWithTag("document_edit_p1").performClick()
        assertEquals("p1", edited?.id)
        compose.onNodeWithTag("document_delete_p1").performClick()
        compose.onNodeWithTag("document_delete_confirm").performClick()
        assertEquals("p1", deleted?.id)
    }

    @Composable private fun screen() = DocumentsScreen(documents, { _, _ -> }, {}, {}, {}, {})
    private fun doc(id: String, name: String, category: DocumentCategory, type: String, country: String) = TravelDocument(
        id = id, category = category, typeId = category.types.first().id, typeName = type, name = name,
        countryOfIssue = country, createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH
    )
}
