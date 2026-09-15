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

class TripCreateTravelersScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun groupOnlyContentHiddenForSoloAndShownForGroup() {
        composeRule.setContent {
            Check2GoTheme {
                var adventureType by remember { mutableStateOf(TripAdventureType.Solo) }
                TripCreateTravelersScreen(
                    adventureType = adventureType,
                    onAdventureTypeChange = { adventureType = it },
                    petsIncluded = false,
                    onPetsIncludedChange = {},
                    onAddTraveler = {},
                    onBack = {},
                    onComplete = {}
                )
            }
        }

        composeRule.onNodeWithText("My travelers").assertDoesNotExist()
        composeRule.onNodeWithText("Add traveler").assertDoesNotExist()

        composeRule.onNodeWithText("Group adventure").performClick()

        composeRule.onNodeWithText("My travelers").assertIsDisplayed()
        composeRule.onNodeWithText("Add traveler").assertIsDisplayed()

        composeRule.onNodeWithText("Solo adventure").performClick()

        composeRule.onNodeWithText("My travelers").assertDoesNotExist()
        composeRule.onNodeWithText("Add traveler").assertDoesNotExist()
    }

    @Test
    fun petsSelectionTogglesBetweenNoAndYes() {
        composeRule.setContent {
            Check2GoTheme {
                var petsIncluded by remember { mutableStateOf(false) }
                TripCreateTravelersScreen(
                    adventureType = TripAdventureType.Solo,
                    onAdventureTypeChange = {},
                    petsIncluded = petsIncluded,
                    onPetsIncludedChange = { petsIncluded = it },
                    onAddTraveler = {},
                    onBack = {},
                    onComplete = {}
                )
            }
        }

        composeRule.onNodeWithText("Yes").performClick()
        composeRule.onNodeWithText("No").performClick()
    }

    @Test
    fun addTravelerBackAndCompleteEmitCallbacks() {
        var addTravelerRequested = false
        var backRequested = false
        var completeRequested = false
        composeRule.setContent {
            Check2GoTheme {
                TripCreateTravelersScreen(
                    adventureType = TripAdventureType.Group,
                    onAdventureTypeChange = {},
                    petsIncluded = false,
                    onPetsIncludedChange = {},
                    onAddTraveler = { addTravelerRequested = true },
                    onBack = { backRequested = true },
                    onComplete = { completeRequested = true }
                )
            }
        }

        composeRule.onNodeWithText("Add traveler").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        assertTrue(addTravelerRequested)
        assertTrue(backRequested)
        assertTrue(completeRequested)
    }
}
