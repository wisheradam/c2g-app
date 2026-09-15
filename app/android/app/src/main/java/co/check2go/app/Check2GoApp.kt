package co.check2go.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import co.check2go.core.design.AppDestination
import co.check2go.feature.checklists.ChecklistCreateScreen
import co.check2go.feature.checklists.ChecklistDraft
import co.check2go.feature.checklists.ChecklistDraftSaver
import co.check2go.feature.checklists.ChecklistItemDraft
import co.check2go.feature.checklists.ChecklistSection
import co.check2go.feature.checklists.ChecklistsEmptyScreen
import co.check2go.feature.checklists.ChecklistsPopulatedScreen
import co.check2go.feature.checklists.CompletedChecklist
import co.check2go.feature.checklists.CompletedChecklistListSaver
import co.check2go.feature.checklists.withItemAdded
import co.check2go.feature.checklists.withItemRemoved
import co.check2go.feature.checklists.withSectionAdded
import co.check2go.feature.checklists.withSectionRemoved
import co.check2go.feature.home.HomeEmptyScreen
import co.check2go.feature.home.MyTripsScreen
import co.check2go.feature.trip.CompletedTrip
import co.check2go.feature.trip.CompletedTripListSaver
import co.check2go.feature.trip.TripAdventureType
import co.check2go.feature.trip.TripCreateDatesScreen
import co.check2go.feature.trip.TripCreateDestinationScreen
import co.check2go.feature.trip.TripCreateTravelersScreen
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.feature.trip.TripFilter
import co.check2go.feature.trip.TripTravelersDraft

private enum class AppScreen { Home, Checklists, ChecklistCreate, TripDestination, TripDates, TripTravelers }

/**
 * Minimal app-level navigation for HOME_EMPTY/HOME_TRIPS <-> CHECKLISTS_EMPTY/CHECKLISTS_POPULATED
 * <-> CHECKLIST_CREATE and HOME_EMPTY/HOME_TRIPS -> TRIP_CREATE_DESTINATION -> TRIP_CREATE_DATES ->
 * TRIP_CREATE_TRAVELERS.
 *
 * [onChecklistCreated] mirrors [onTripCreateComplete]: an app-level hook fired once CHECKLIST_CREATE
 * "Save changes" (Flow 7, step 7) succeeds, receiving the full structured draft.
 */
@Composable
fun Check2GoApp(
    onTripCreateComplete: (TripDestinationDraft, TripDatesDraft, TripTravelersDraft) -> Unit,
    onChecklistCreated: (ChecklistDraft) -> Unit = {}
) {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Home) }

    var destinationCountry by rememberSaveable { mutableStateOf("") }
    var departureCountry by rememberSaveable { mutableStateOf("") }
    var tripName by rememberSaveable { mutableStateOf("") }

    var oneWay by rememberSaveable { mutableStateOf(false) }
    var departureDate by rememberSaveable { mutableStateOf("") }
    var returnDate by rememberSaveable { mutableStateOf("") }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }

    var adventureType by rememberSaveable { mutableStateOf(TripAdventureType.Solo) }
    var petsIncluded by rememberSaveable { mutableStateOf(false) }

    var trips by rememberSaveable(stateSaver = CompletedTripListSaver) {
        mutableStateOf(emptyList<CompletedTrip>())
    }
    var nextTripId by rememberSaveable { mutableStateOf(1L) }
    var tripFilter by rememberSaveable { mutableStateOf(TripFilter.Active) }

    // CHECKLIST_CREATE draft: hoisted here (rather than local to the screen) so Back navigating
    // out to CHECKLISTS_EMPTY/CHECKLISTS_POPULATED and reopening Create during the same app state
    // shows the same in-progress draft instead of silently discarding it. "Save changes" is the
    // only thing that resets it back to a fresh draft.
    var checklistDraft by rememberSaveable(stateSaver = ChecklistDraftSaver) {
        mutableStateOf(ChecklistDraft())
    }
    var nextSectionId by rememberSaveable { mutableStateOf(1L) }
    var nextItemId by rememberSaveable { mutableStateOf(1L) }
    var newSectionName by rememberSaveable { mutableStateOf("") }
    var newItemName by rememberSaveable { mutableStateOf("") }
    var newItemSectionId by rememberSaveable { mutableStateOf<Long?>(null) }
    var newItemIncludeFile by rememberSaveable { mutableStateOf(false) }

    var checklists by rememberSaveable(stateSaver = CompletedChecklistListSaver) {
        mutableStateOf(emptyList<CompletedChecklist>())
    }
    var nextChecklistId by rememberSaveable { mutableStateOf(1L) }

    fun resetTripDraft() {
        destinationCountry = ""
        departureCountry = ""
        tripName = ""
        oneWay = false
        departureDate = ""
        returnDate = ""
        reminderEnabled = false
        adventureType = TripAdventureType.Solo
        petsIncluded = false
    }

    val startNewTripDraft = {
        resetTripDraft()
        screen = AppScreen.TripDestination
    }

    // Documents/Events have no DOCUMENTS_HOME/EVENTS_HOME screen yet (docs/screen-inventory.md),
    // so selecting them is a no-op boundary rather than switching to a fake screen.
    val onAppDestinationSelected: (AppDestination) -> Unit = { destination ->
        when (destination) {
            AppDestination.Home -> screen = AppScreen.Home
            AppDestination.Checklists -> screen = AppScreen.Checklists
            AppDestination.Documents, AppDestination.Events -> {}
        }
    }

    when (screen) {
        AppScreen.Home -> {
            if (trips.isEmpty()) {
                HomeEmptyScreen(
                    onAddTrip = startNewTripDraft,
                    onQuickAdd = startNewTripDraft,
                    onDestinationSelected = onAppDestinationSelected
                )
            } else {
                MyTripsScreen(
                    trips = trips,
                    filter = tripFilter,
                    onFilterChange = { tripFilter = it },
                    onAddTrip = startNewTripDraft,
                    onQuickAdd = startNewTripDraft,
                    onDestinationSelected = onAppDestinationSelected
                )
            }
        }

        AppScreen.Checklists -> {
            // Checklists is a peer main tab reached from the shared bottom nav, not a pushed
            // screen, so Back is handled purely via BackHandler (no in-screen back affordance) and
            // returns to Home rather than the default system behavior (e.g. exiting the app).
            BackHandler(onBack = { screen = AppScreen.Home })
            val openChecklistCreate = { screen = AppScreen.ChecklistCreate }
            if (checklists.isEmpty()) {
                ChecklistsEmptyScreen(
                    onCreateChecklist = openChecklistCreate,
                    onQuickAdd = openChecklistCreate,
                    onDestinationSelected = onAppDestinationSelected
                )
            } else {
                ChecklistsPopulatedScreen(
                    checklists = checklists,
                    onCreateChecklist = openChecklistCreate,
                    onQuickAdd = openChecklistCreate,
                    onDestinationSelected = onAppDestinationSelected
                )
            }
        }

        AppScreen.ChecklistCreate -> {
            val navigateToChecklists = { screen = AppScreen.Checklists }
            // Back preserves checklistDraft as-is (no reset here): reopening Create afterwards
            // shows the same in-progress draft. Only "Save changes" below clears it.
            BackHandler(onBack = navigateToChecklists)
            ChecklistCreateScreen(
                draft = checklistDraft,
                onNameChange = { checklistDraft = checklistDraft.copy(name = it) },
                newSectionName = newSectionName,
                onNewSectionNameChange = { newSectionName = it },
                onAddSection = {
                    if (newSectionName.isNotBlank()) {
                        checklistDraft = checklistDraft.withSectionAdded(
                            ChecklistSection(id = nextSectionId, name = newSectionName.trim())
                        )
                        nextSectionId += 1
                        newSectionName = ""
                    }
                },
                onRemoveSection = { sectionId ->
                    checklistDraft = checklistDraft.withSectionRemoved(sectionId)
                    if (newItemSectionId == sectionId) newItemSectionId = null
                },
                newItemName = newItemName,
                onNewItemNameChange = { newItemName = it },
                newItemSectionId = newItemSectionId,
                onNewItemSectionIdChange = { newItemSectionId = it },
                newItemIncludeFile = newItemIncludeFile,
                onNewItemIncludeFileChange = { newItemIncludeFile = it },
                onAddItem = {
                    if (newItemName.isNotBlank()) {
                        checklistDraft = checklistDraft.withItemAdded(
                            ChecklistItemDraft(
                                id = nextItemId,
                                name = newItemName.trim(),
                                sectionId = newItemSectionId,
                                includeFile = newItemIncludeFile
                            )
                        )
                        nextItemId += 1
                        newItemName = ""
                        newItemSectionId = null
                        newItemIncludeFile = false
                    }
                },
                onRemoveItem = { itemId -> checklistDraft = checklistDraft.withItemRemoved(itemId) },
                onBack = navigateToChecklists,
                onSave = {
                    val savedDraft = checklistDraft
                    onChecklistCreated(savedDraft)
                    checklists = checklists + CompletedChecklist(id = nextChecklistId, name = savedDraft.name)
                    nextChecklistId += 1
                    checklistDraft = ChecklistDraft()
                    newSectionName = ""
                    newItemName = ""
                    newItemSectionId = null
                    newItemIncludeFile = false
                    screen = AppScreen.Checklists
                }
            )
        }

        AppScreen.TripDestination -> {
            val navigateHome = { screen = AppScreen.Home }
            BackHandler(onBack = navigateHome)
            TripCreateDestinationScreen(
                destinationCountry = destinationCountry,
                onDestinationCountryChange = { destinationCountry = it },
                departureCountry = departureCountry,
                onDepartureCountryChange = { departureCountry = it },
                tripName = tripName,
                onTripNameChange = { tripName = it },
                onAutofill = {},
                onBack = navigateHome,
                onStart = { screen = AppScreen.TripDates }
            )
        }

        AppScreen.TripDates -> {
            val navigateToDestination = { screen = AppScreen.TripDestination }
            BackHandler(onBack = navigateToDestination)
            TripCreateDatesScreen(
                oneWay = oneWay,
                onOneWayChange = { oneWay = it },
                departureDate = departureDate,
                onDepartureDateChange = { departureDate = it },
                returnDate = returnDate,
                onReturnDateChange = { returnDate = it },
                reminderEnabled = reminderEnabled,
                onReminderEnabledChange = { reminderEnabled = it },
                onLoadTicket = {},
                onBack = navigateToDestination,
                onNextStep = { screen = AppScreen.TripTravelers }
            )
        }

        AppScreen.TripTravelers -> {
            val navigateToDates = { screen = AppScreen.TripDates }
            BackHandler(onBack = navigateToDates)
            TripCreateTravelersScreen(
                adventureType = adventureType,
                onAdventureTypeChange = { adventureType = it },
                petsIncluded = petsIncluded,
                onPetsIncludedChange = { petsIncluded = it },
                onAddTraveler = {},
                onBack = navigateToDates,
                onComplete = {
                    val destinationDraft = TripDestinationDraft(
                        destinationCountry = destinationCountry,
                        departureCountry = departureCountry,
                        tripName = tripName
                    )
                    val datesDraft = TripDatesDraft(
                        oneWay = oneWay,
                        departureDate = departureDate,
                        returnDate = returnDate,
                        reminderEnabled = reminderEnabled
                    )
                    val travelersDraft = TripTravelersDraft(
                        adventureType = adventureType,
                        petsIncluded = petsIncluded
                    )

                    trips = trips + CompletedTrip(
                        id = nextTripId,
                        tripName = tripName,
                        destinationCountry = destinationCountry,
                        departureCountry = departureCountry,
                        oneWay = oneWay,
                        departureDate = departureDate,
                        returnDate = returnDate
                    )
                    nextTripId += 1
                    resetTripDraft()
                    // Complete navigates straight to Home/MY_TRIPS, and Home registers no
                    // BackHandler back into the wizard, so a Back press from MY_TRIPS cannot
                    // return to TRIP_CREATE_TRAVELERS and resubmit this trip.
                    screen = AppScreen.Home

                    onTripCreateComplete(destinationDraft, datesDraft, travelersDraft)
                }
            )
        }
    }
}
