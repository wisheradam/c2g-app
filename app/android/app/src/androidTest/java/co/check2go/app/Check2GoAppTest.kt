package co.check2go.app

import android.view.KeyEvent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
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
    fun openingTravelReminderTogglesNavigatesToTheReminderPicker() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()

        composeRule.onNodeWithTag("reminder_set_action").assertIsDisplayed()
        composeRule.onNodeWithText("Reminder date").assertIsDisplayed()
        composeRule.onNodeWithText("Reminder time").assertIsDisplayed()
    }

    @Test
    fun confirmingAReminderReturnsToDatesEnabledWithItsSummaryShown() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOn()
        composeRule.onNodeWithText("Reminder: 2026-09-25 at 08:00").assertIsDisplayed()
    }

    @Test
    fun backFromReminderPickerDoesNotEnableTheReminderAndPreservesTheTripDraft() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")

        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOff()
        composeRule.onNodeWithText("2026-10-01").assertIsDisplayed()
    }

    @Test
    fun reopeningThePickerAfterConfirmingPrefillsTheLastConfirmedReminder() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()

        composeRule.onNodeWithText("Edit").performClick()

        composeRule.onNodeWithText("2026-09-25").assertIsDisplayed()
        composeRule.onNodeWithText("08:00").assertIsDisplayed()
    }

    @Test
    fun editingAConfirmedReminderAndCancelingLeavesThePreviousConfirmedValueInPlace() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()

        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextClearance()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-11-11")
        composeRule.onNodeWithText("Back").performClick()

        // The unconfirmed edit is discarded; the original confirmed reminder is still shown.
        composeRule.onNodeWithText("Reminder: 2026-09-25 at 08:00").assertIsDisplayed()
    }

    @Test
    fun disablingAConfirmedReminderTurnsItOffWithoutOpeningThePickerAndKeepsTheTripDraft() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()

        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()

        composeRule.onNodeWithText("Travel dates").assertIsDisplayed()
        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOff()
        composeRule.onNodeWithText("Reminder: 2026-09-25 at 08:00").assertDoesNotExist()
        composeRule.onNodeWithText("2026-10-01").assertIsDisplayed()
    }

    @Test
    fun reEnablingAPreviouslyDisabledReminderPrefillsItsLastConfirmedValue() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()

        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()

        composeRule.onNodeWithText("2026-09-25").assertIsDisplayed()
        composeRule.onNodeWithText("08:00").assertIsDisplayed()
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
    fun completeEmitsTheConfirmedReminderDateAndTimeInTheDatesDraft() {
        var dates: TripDatesDraft? = null
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, dt, _ -> dates = dt })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithText("Return date").performTextInput("2026-10-10")
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()
        composeRule.onNodeWithText("Next step").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        assertEquals(
            TripDatesDraft(
                oneWay = false,
                departureDate = "2026-10-01",
                returnDate = "2026-10-10",
                reminderEnabled = true,
                reminderDate = "2026-09-25",
                reminderTime = "08:00"
            ),
            dates
        )
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
    fun startingAnotherTripResetsAnyConfirmedReminderFromThePreviousDraft() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        fillDestinationAndStart()
        composeRule.onNodeWithTag("trip_reminder_toggle").performClick()
        composeRule.onNodeWithTag("reminder_date_field").performTextInput("2026-09-25")
        composeRule.onNodeWithTag("reminder_time_field").performTextInput("08:00")
        composeRule.onNodeWithTag("reminder_set_action").performClick()
        composeRule.onNodeWithText("Departure date").performTextInput("2026-10-01")
        composeRule.onNodeWithText("Return date").performTextInput("2026-10-10")
        composeRule.onNodeWithText("Next step").performClick()
        composeRule.onNodeWithText("Complete").performClick()

        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithText("Destination country").performTextInput("Norway")
        composeRule.onNodeWithText("Departure country").performTextInput("Spain")
        composeRule.onNodeWithText("Trip name").performTextInput("Winter escape")
        composeRule.onNodeWithText("Start").performClick()

        composeRule.onNodeWithTag("trip_reminder_toggle").assertIsOff()
        composeRule.onNodeWithText("Reminder: 2026-09-25 at 08:00").assertDoesNotExist()
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
        composeRule.onNodeWithTag("new_item_name_field").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

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
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        composeRule.onNodeWithText("Create checklist").performClick()

        composeRule.onNodeWithText("Before leaving").assertDoesNotExist()
    }

    private fun createChecklistWithOneItem(checklistName: String, itemName: String) {
        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput(checklistName)
        composeRule.onNodeWithTag("new_item_name_field").performTextInput(itemName)
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()
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

    @Test
    fun editChecklistOpensEditorPrefilledWithSavedNameAndItems() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()

        composeRule.onNodeWithText("Edit checklist").performClick()

        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_item_row_1").assertIsDisplayed()
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
    }

    @Test
    fun editingAChecklistPreservesExistingCompletionAndStartsAnAddedItemIncomplete() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()
        composeRule.onNodeWithText("100% complete").assertIsDisplayed()

        composeRule.onNodeWithText("Edit checklist").performClick()
        composeRule.onNodeWithTag("new_item_name_field").performTextInput("Charger")
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        // Back on CHECKLIST_DETAIL: the pre-existing item kept its completion, the added item
        // (a new id never seen on this checklist) starts uncompleted.
        composeRule.onNodeWithText("50% complete").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").assertIsOn()
        composeRule.onNodeWithTag("checklist_detail_item_row_2").assertIsOff()
    }

    @Test
    fun editingAChecklistCanRenameItAndRemoveAnItem() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        composeRule.onNodeWithText("Before leaving").performTextClearance()
        composeRule.onNodeWithText("Checklist name").performTextInput("Packing list")
        composeRule.onNodeWithContentDescription("Remove item Passport").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        // Detail reflects both the rename and the removal.
        composeRule.onNodeWithText("Packing list").assertIsDisplayed()
        composeRule.onNodeWithText("Passport").assertDoesNotExist()
        composeRule.onNodeWithText("No items in this checklist yet.").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()

        // Populated list is the same shared source of truth, so it reflects the rename too.
        composeRule.onNodeWithText("Packing list").assertIsDisplayed()
        composeRule.onNodeWithText("Before leaving").assertDoesNotExist()
    }

    @Test
    fun backFromChecklistEditDiscardsUnsavedNameAndItemChanges() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        composeRule.onNodeWithText("Checklist name").performTextInput(" (edited)")
        composeRule.onNodeWithContentDescription("Remove item Passport").performClick()

        composeRule.onNodeWithText("Back").performClick()

        // Back discarded both the unsaved rename and the unsaved item removal.
        composeRule.onAllNodesWithText("Before leaving").assertCountEquals(1)
        composeRule.onNodeWithText("Passport").assertIsDisplayed()

        // Reopening Edit re-seeds from the saved checklist, not the discarded in-memory edit.
        composeRule.onNodeWithText("Edit checklist").performClick()
        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_item_row_1").assertIsDisplayed()
    }

    @Test
    fun editingAChecklistCanRenameAnExistingSection() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()
        composeRule.onNodeWithTag("new_item_name_field").performTextInput("Passport")
        composeRule.onNodeWithTag("new_item_section_field").performClick()
        composeRule.onNodeWithTag("new_item_section_field_option_1").performClick()
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        // Rename the existing section in place -- not remove-and-re-add, so the item's reference
        // to its id (set above) survives untouched.
        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextClearance()
        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextInput("Paperwork")
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        // Detail reflects the rename, and the item is still grouped under the (renamed) section.
        composeRule.onNodeWithText("Paperwork").assertIsDisplayed()
        composeRule.onNodeWithText("Documents").assertDoesNotExist()
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
    }

    @Test
    fun editingAChecklistCanReassignAnExistingItemsSectionAndToggleIncludeFile() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()
        composeRule.onNodeWithTag("new_item_name_field").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        // The item was added above with no section and include-file off; edit both fields on the
        // existing item in place (not via remove-and-re-add).
        composeRule.onNodeWithTag("checklist_item_section_field_1").performClick()
        composeRule.onNodeWithTag("checklist_item_section_field_1_option_1").performClick()
        composeRule.onNodeWithTag("checklist_item_include_file_toggle_1")
            .performScrollTo()
            .performClick()
            .assertIsOn()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        // Detail shows the item grouped under Documents (its now-saved section) and marked with
        // the include-file note.
        composeRule.onNodeWithText("Documents").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_detail_upload_file_note_1").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun backFromChecklistEditDiscardsSectionRenameItemSectionReassignmentAndIncludeFileChange() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        composeRule.onNodeWithText("Checklists").performClick()
        composeRule.onNodeWithText("Create checklist").performClick()
        composeRule.onNodeWithText("Checklist name").performTextInput("Before leaving")
        composeRule.onNodeWithText("Section name").performTextInput("Documents")
        composeRule.onNodeWithText("Add section").performClick()
        composeRule.onNodeWithTag("new_item_name_field").performTextInput("Passport")
        composeRule.onNodeWithText("Add item").performClick()
        composeRule.onNodeWithText("Save changes").performScrollTo().performClick()

        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        composeRule.onNodeWithTag("checklist_section_name_field_1").performTextInput(" (edited)")
        composeRule.onNodeWithTag("checklist_item_section_field_1").performClick()
        composeRule.onNodeWithTag("checklist_item_section_field_1_option_1").performClick()
        composeRule.onNodeWithTag("checklist_item_include_file_toggle_1")
            .performScrollTo()
            .performClick()
            .assertIsOn()

        composeRule.onNodeWithText("Back").performClick()

        // The item's section/include-file were never saved, so detail shows no file note.
        composeRule.onNodeWithTag("checklist_detail_upload_file_note_1").assertDoesNotExist()

        // Reopening Edit re-seeds from the saved checklist, not the discarded in-memory edit: the
        // section keeps its original name, and the item is still unassigned with include-file off.
        composeRule.onNodeWithText("Edit checklist").performClick()
        composeRule.onNodeWithTag("checklist_section_name_field_1").assertTextContains("Documents", substring = true)
        composeRule.onNodeWithTag("checklist_item_section_field_1").assertTextContains("No section", substring = true)
        composeRule.onNodeWithTag("checklist_item_include_file_toggle_1").assertIsOff()
    }

    @Test
    fun duplicatingFromDetailShowsConfirmationAndGoBackReturnsToTheSourceDetailUnchanged() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()
        composeRule.onNodeWithText("100% complete").assertIsDisplayed()

        composeRule.onNodeWithText("Duplicate checklist").performClick()

        composeRule.onNodeWithText("A copy has been created").assertIsDisplayed()
        composeRule.onNodeWithText("Go back").performClick()

        // Back on the source's own CHECKLIST_DETAIL, unaffected by the duplication.
        composeRule.onNodeWithTag("checklist_detail_completion").assertTextContains("100% complete")
        composeRule.onNodeWithTag("checklist_detail_item_row_1").assertIsOn()
    }

    @Test
    fun duplicatingFromDetailUseNowOpensTheCopysOwnDetailWithCompletionReset() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()
        composeRule.onNodeWithText("100% complete").assertIsDisplayed()

        composeRule.onNodeWithText("Duplicate checklist").performClick()
        composeRule.onNodeWithText("Use now").performClick()

        // The copy's own CHECKLIST_DETAIL: same title/items, but completion reset (a fresh,
        // unstarted checklist), not the 100% the source item was toggled to.
        composeRule.onAllNodesWithText("Before leaving").assertCountEquals(1)
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
        composeRule.onNodeWithText("0% complete").assertIsDisplayed()

        // The copy's item is a brand-new SavedChecklistItem drawn from the shared nextItemId
        // counter (CompletedChecklist.duplicate), not a reuse of the source item's id -- so its
        // row tag is "_2", the next id after the source item's own "_1".
        composeRule.onNodeWithTag("checklist_detail_item_row_2").assertIsOff()
    }

    @Test
    fun duplicateIsASeparatelySavedCopyThatAppearsAlongsideTheUnaffectedSourceInThePopulatedList() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithTag("checklist_detail_item_row_1").performClick()

        composeRule.onNodeWithText("Duplicate checklist").performClick()
        composeRule.onNodeWithText("Use now").performClick()
        composeRule.onNodeWithText("Back").performClick()

        // Two distinct rows on CHECKLISTS_POPULATED: the original (still 100%) and the new copy
        // (0%), proving the copy is its own saved checklist rather than replacing the source.
        composeRule.onNodeWithTag("checklist_card_1").assertIsDisplayed()
        composeRule.onNodeWithTag("checklist_card_2").assertIsDisplayed()
        composeRule.onAllNodesWithText("Before leaving").assertCountEquals(2)
        composeRule.onAllNodesWithText("100% complete").assertCountEquals(1)
        composeRule.onAllNodesWithText("0% complete").assertCountEquals(1)
    }

    @Test
    fun duplicatingFromTheEditorDuplicatesTheSavedChecklistAndGoBackReturnsToTheEditor() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        // Unsaved in-progress rename: Duplicate should copy the saved checklist ("Before leaving"),
        // not this unsaved edit.
        composeRule.onNodeWithText("Before leaving").performTextClearance()
        composeRule.onNodeWithText("Checklist name").performTextInput("Renamed but unsaved")

        composeRule.onNodeWithText("Duplicate checklist").performClick()

        composeRule.onNodeWithText("A copy has been created").assertIsDisplayed()
        composeRule.onNodeWithText("Before leaving").assertIsDisplayed()

        composeRule.onNodeWithText("Go back").performClick()

        // Back on CHECKLIST_EDIT, with the unsaved rename still in place (Duplicate did not save
        // or discard it).
        composeRule.onNodeWithText("Renamed but unsaved").assertIsDisplayed()
    }

    @Test
    fun duplicatingFromTheEditorUseNowOpensTheCopysDetailLeavingTheEditsSourceUnsaved() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Edit checklist").performClick()

        composeRule.onNodeWithText("Duplicate checklist").performClick()
        composeRule.onNodeWithText("Use now").performClick()

        // The duplicate's own detail, carrying the item forward.
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
        composeRule.onNodeWithText("0% complete").assertIsDisplayed()

        composeRule.onNodeWithText("Back").performClick()

        // Both the source and the copy exist; the source was never renamed since Edit's "Save
        // changes" was never pressed.
        composeRule.onAllNodesWithText("Before leaving").assertCountEquals(2)
    }

    @Test
    fun systemBackFromDuplicateConfirmationBehavesLikeGoBack() {
        composeRule.setContent {
            Check2GoTheme {
                Check2GoApp(onTripCreateComplete = { _, _, _ -> })
            }
        }

        createChecklistWithOneItem("Before leaving", "Passport")
        composeRule.onNodeWithText("Before leaving").performClick()
        composeRule.onNodeWithText("Duplicate checklist").performClick()
        composeRule.onNodeWithText("A copy has been created").assertIsDisplayed()

        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)

        // Back on the source's CHECKLIST_DETAIL, same as tapping "Go back" would do.
        composeRule.onNodeWithText("Passport").assertIsDisplayed()
        composeRule.onNodeWithText("Edit checklist").assertIsDisplayed()
    }
}
