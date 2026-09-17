package co.check2go.feature.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithTag
import co.check2go.ui.theme.Check2GoTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class AccountProfileScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun profileDraftValidatesAndMapsAllCoreProfileFields() {
        val draft = ProfileDraft(
            firstName = " Ada ", lastName = " Lovelace ", middleName = " Byron ",
            gender = GenderOption.Female, dateOfBirth = "1815-12-10",
            addressLine1 = "12 St James's Square", city = "London", addressCountryCode = "gb",
            citizenshipCountryCodes = setOf("gb"), residenceCountryCode = "gb",
            residencePlace = "London", residencyStatus = ResidencyStatus.Citizen
        )

        assertNull(draft.validationError(LocalDate.of(2026, 1, 1)))
        val account = draft.applyTo(PersonalAccount())
        assertEquals(PersonName("Ada", "Lovelace", "Byron"), account.name)
        assertEquals(LocalDate.of(1815, 12, 10), account.dateOfBirth)
        assertEquals(setOf("GB"), account.citizenshipCountryCodes)
        assertEquals(ResidencyStatus.Citizen, account.residence?.status)
    }

    @Test
    fun invalidDateIsShownAndDoesNotSave() {
        var saved: PersonalAccount? = null
        compose.setContent {
            Check2GoTheme {
                AccountProfileScreen(
                    account = PersonalAccount(),
                    onBack = {},
                    onSave = { saved = it },
                    referenceDate = LocalDate.of(2026, 1, 1)
                )
            }
        }

        compose.onNodeWithText("First name").performTextInput("Ada")
        compose.onNodeWithText("Last name").performTextInput("Lovelace")
        compose.onNodeWithText("Date of birth (YYYY-MM-DD)").performTextInput("tomorrow")
        compose.onNodeWithTag("account_profile").performScrollToIndex(24)
        compose.onNodeWithTag("account_save").performClick()

        compose.onNodeWithText("Use date format YYYY-MM-DD").assertIsDisplayed()
        assertNull(saved)
    }
}
