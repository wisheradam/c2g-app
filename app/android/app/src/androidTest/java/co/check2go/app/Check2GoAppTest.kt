package co.check2go.app

import android.view.KeyEvent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import co.check2go.feature.checklists.ChecklistDraft
import co.check2go.feature.trip.TripAdventureType
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.feature.trip.TripTravelersDraft
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class Check2GoAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun fillDestinationAndStart() {
        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Destination country").performTextInput("Italy")
        composeRule.onNodeWithText("Departure country").performTextInput("Israel")
        composeRule.onNodeWithText("Trip name").performTextInput("Summer trip")
        composeRule.onNodeWithText("Start").performClick()
    }

    private fun fillDestinationDatesAndAdvanceToTravelers() {
        fillDestinationAndStart()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithText("Return date").performTextInput("2026-10-10")
        composeRule.onNodeWithText("Next step").performClick()
    }

    @Test
    fun addTripOpensDestinationAndBackReturnsHome() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Start your first journey!").assertIsDisplayed()
    }

    @Test
    fun startOnDestinationOpensDatesScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithText("Quick Fill").assertIsDisplayed()
    }

    @Test
    fun backFromDatesPreservesDestinationDraft() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Italy").assertIsDisplayed()
        composeRule.onNodeWithText("Israel").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun nextStepFromDatesOpensTravelersScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()

        composeRule.onNodeWithText("Solo adventure").assertIsDisplayed()
        composeRule.onNodeWithText("Group adventure").assertIsDisplayed()
        composeRule.onNodeWithText("Complete").assertIsDisplayed()
    }

    @Test
    fun backFromTravelersPreservesDestinationAndDatesDrafts() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Group adventure").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-01").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-10").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Italy").assertIsDisplayed()
        composeRule.onNodeWithText("Israel").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun travelersStatePreservedWhenReturningFromDates() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Group adventure").performClick()
        composeRule.onNodeWithText("My travelers").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Next step").performClick()

        composeRule.onNodeWithText("My travelers").assertIsDisplayed()
    }

    @Test
    fun completeEmitsDestinationDatesAndTravelersDrafts() {
        var destination: TripDestinationDraft? = null
        var dates: TripDatesDraft? = null
        var travelers: TripTravelersDraft? = null
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(
                    onTripCreateComplete = { d, dt, tr ->
                        destination = d
                        dates = dt
                        travelers = tr
                    }
                )
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Group adventure").performClick()
        composeRule.onNodeWithText("Yes").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        assertEquals(TripDestinationDraft("Italy", "Israel", "Summer trip"), destination)
        assertEquals(TripDatesDraft(false, "2026-10-01", "2026-10-10", false), dates)
        assertEquals(TripTravelersDraft(TripAdventureType.Group, true), travelers)
    }

    @Test
    fun completingTripNavigatesToMyTripsWithVisibleSummary() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("My trips").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
        composeRule.onNodeWithText("Israel → Italy").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-01 – 2026-10-10").assertIsDisplayed()
    }

    @Test
    fun bothActiveAndAllFiltersShowTheCompletedTrip() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("All").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("Active").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun startingAnotherTripFromMyTripsResetsDraftAndKeepsCompletedTrip() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("Add a trip").performClick()

        composeRule.onNodeWithText("Choose your destination country").assertIsDisplayed()
        composeRule.onNodeWithText("Italy").assertDoesNotExist()
        composeRule.onNodeWithText("Israel").assertDoesNotExist()
        composeRule.onNodeWithText("Summer trip").assertDoesNotExist()

        composeRule.onNodeWithText("Destination country").performTextInput("Norway")
        composeRule.onNodeWithText("Departure country").performTextInput("Spain")
        composeRule.onNodeWithText("Trip name").performTextInput("Winter escape")
        composeRule.onNodeWithText("Start").performClick()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-12-05")
        composeRule.onNodeWithText("Return date").performTextInput("2026-12-12")
        composeRule.onNodeWithText("Next step").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
        composeRule.onNodeWithText("Winter escape").assertIsDisplayed()
    }

    @Test
    fun tappingChecklistsTabFromHomeOpensChecklistsEmptyScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()

        composeRule.onNodeWithText("No active checklists").assertIsDisplayed()
    }

    @Test
    fun checklistsTabShowsSelectedStateAndHomeTabDoesNot() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()

        composeRule.onNodeWithTag("nav_checklists").assertIsSelected()
        composeRule.onNodeWithTag("nav_home").assertIsNotSelected()
    }

    @Test
    fun backFromChecklistsReturnsToHome() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("No active checklists").assertIsDisplayed()

        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)

        composeRule.onNodeWithText("Start your first journey!").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_home").assertIsSelected()
    }

    @Test
    fun myTripsStateIsRetainedWhenSwitchingToChecklistsAndBack() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationDatesAndAdvanceToTravelers()
        composeRule.onNodeWithText("Complete").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("No active checklists").assertIsDisplayed()

        composeRule.onNodeWithText("Home").performClick()

        composeRule.onNodeWithText("My trips").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun createChecklistCtaOpensChecklistCreateScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()

        composeRule.onNodeWithText("Checklist name").assertIsDisplayed()
    }

    @Test
    fun checklistsQuickAddOpensChecklistCreateScreen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithContentDescription("Quick add").performClick()

        composeRule.onNodeWithText("Checklist name").assertIsDisplayed()
    }

    @Test
    fun backFromChecklistCreatePreservesDraftOnReopen() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")

        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("No active checklists").assertIsDisplayed()

        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
    }

    @Test
    fun saveChecklistEmitsDraftNavigatesToPopulatedListAndClearsDraft() {
        var savedDraft: ChecklistDraft? = null

        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(
                    onTripCreateComplete = { _, _, _ -> },
                    onChecklistCreated = { savedDraft = it }
                )
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Item name").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performClick()

        assertEquals("Before leaving", savedDraft?.name)
        assertEquals(1, savedDraft?.items?.size)
        assertEquals("Passport", savedDraft?.items?.first()?.name)

        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        composeRule.onNodeWithText("Personal checklist").assertIsDisplayed()
        composeRule.onNodeWithText("0% complete").assertIsDisplayed()
    }

    @Test
    fun creatingAnotherChecklistFromPopulatedListStartsWithAFreshDraft() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Save changes").performClick()

        composeRule.onNodeWithText("Create checklist").performClick()

        composeRule.onNodeWithText("Before leaving").assertDoesNotExist()
    }

    private fun createChecklistWithOneItem(checklistName: String, itemName: String) {
        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput(checklistName)
        composeRule.onNodeWithText("Item name").performTextInput(itemName)
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performClick()
    }

    @Test
    fun tappingASavedChecklistOpensDetailWithItsStructuredItemsUncompleted() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()

        // Detail title is the checklist's own name, and the just-saved item is present and unchecked.
        composeRule.onAllNodesWithText("Before leaving").assertCountEquals(1)
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
        composeRule.onNodeWithText("0% complete").assertIsDisplayed()
    }

    @Test
    fun togglingAnItemInDetailUpdatesCompletionAndBackReturnsToThePopulatedList() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()

        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()
        composeRule.onNodeWithText("100% complete").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()

        // Populated list reflects the same updated completion (single shared source of truth).
        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        composeRule.onNodeWithText("100% complete").assertIsDisplayed()
    }

    @Test
    fun toggleRetainedWhenReopeningDetailAfterGoingBack() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()
        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Before leaving").performClick()

        composeRule.onNodeWithText("100% complete").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").assertIsOn()
    }
}
