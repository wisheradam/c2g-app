package co.check2go.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.feature.checklists.ChecklistCreateScreen
import co.check2go.feature.checklists.ChecklistDetailScreen
import co.check2go.feature.checklists.ChecklistDraft
import co.check2go.feature.checklists.ChecklistDraftSaver
import co.check2go.feature.checklists.ChecklistDuplicateConfirmationScreen
import co.check2go.feature.checklists.ChecklistItemDraft
import co.check2go.feature.checklists.ChecklistSection
import co.check2go.feature.checklists.ChecklistsEmptyScreen
import co.check2go.feature.checklists.ChecklistsPopulatedScreen
import co.check2go.feature.checklists.CompletedChecklist
import co.check2go.feature.checklists.CompletedChecklistListSaver
import co.check2go.feature.checklists.duplicate
import co.check2go.feature.checklists.toDraft
import co.check2go.feature.checklists.toSavedChecklist
import co.check2go.feature.checklists.withDraftApplied
import co.check2go.feature.checklists.withItemAdded
import co.check2go.feature.checklists.withItemCompletionToggled
import co.check2go.feature.checklists.withItemIncludeFileChanged
import co.check2go.feature.checklists.withItemNameChanged
import co.check2go.feature.checklists.withItemRemoved
import co.check2go.feature.checklists.withItemSectionChanged
import co.check2go.feature.checklists.withSectionAdded
import co.check2go.feature.checklists.withSectionRemoved
import co.check2go.feature.checklists.withSectionRenamed
import co.check2go.feature.checklists.ShareChecklistScreen
import co.check2go.feature.account.AccountProfileScreen
import co.check2go.feature.account.PersonalAccount
import co.check2go.feature.account.FamilyMembersScreen
import co.check2go.feature.account.InterestsScreen
import co.check2go.feature.account.PrivacyConsentScreen
import co.check2go.feature.home.HomeEmptyScreen
import co.check2go.feature.home.MyTripsScreen
import co.check2go.feature.events.EventCalendarScreen
import co.check2go.feature.trip.CompletedTrip
import co.check2go.feature.trip.CompletedTripListSaver
import co.check2go.feature.trip.ReminderPickerScreen
import co.check2go.feature.trip.TripAdventureType
import co.check2go.feature.trip.TripCreateDatesScreen
import co.check2go.feature.trip.TripCreateDestinationScreen
import co.check2go.feature.trip.TripCreateTravelersScreen
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.feature.trip.TripFilter
import co.check2go.feature.trip.TripTravelersDraft
import co.check2go.feature.trip.TripHubScreen
import co.check2go.feature.trip.MyTravelersScreen
import co.check2go.feature.trip.TravelerEditorScreen

private enum class AppScreen {
    Home,
    Checklists,
    ChecklistCreate,
    ChecklistDetail,
    ChecklistEdit,
    ChecklistDuplicateConfirmation,
    ShareChecklist,
    TripDestination,
    TripDates,
    ReminderPicker,
    TripTravelers,
    MyTravelers,
    TravelerEditor,
    TripHub,
    EventsCalendar,
    AccountProfile,
    FamilyMembers,
    Interests,
    PrivacyConsent
}

/**
 * Minimal app-level navigation for HOME_EMPTY/HOME_TRIPS <-> CHECKLISTS_EMPTY/CHECKLISTS_POPULATED
 * <-> CHECKLIST_CREATE, CHECKLISTS_POPULATED -> CHECKLIST_DETAIL <-> CHECKLIST_EDIT, CHECKLIST_DETAIL
 * / CHECKLIST_EDIT -> CHECKLIST_DUPLICATE_CONFIRMATION -> (back to that same context, or into the
 * duplicate's own CHECKLIST_DETAIL), and HOME_EMPTY/HOME_TRIPS -> TRIP_CREATE_DESTINATION ->
 * TRIP_CREATE_DATES <-> REMINDER_PICKER -> TRIP_CREATE_TRAVELERS.
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
    var personalAccount by remember { mutableStateOf(PersonalAccount()) }

    var destinationCountry by rememberSaveable { mutableStateOf("") }
    var departureCountry by rememberSaveable { mutableStateOf("") }
    var tripName by rememberSaveable { mutableStateOf("") }

    var oneWay by rememberSaveable { mutableStateOf(false) }
    var departureDate by rememberSaveable { mutableStateOf("") }
    var returnDate by rememberSaveable { mutableStateOf("") }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }
    // Last REMINDER_PICKER value confirmed via "Set a reminder" (docs/flows.md Flow 10). Preserved
    // even after reminderEnabled is turned off, so re-enabling the reminder pre-fills it again.
    var reminderDate by rememberSaveable { mutableStateOf("") }
    var reminderTime by rememberSaveable { mutableStateOf("") }
    // REMINDER_PICKER's own in-progress input: seeded from reminderDate/reminderTime when the
    // picker opens, and only written back to them by "Set a reminder" -- Back/system-back discards
    // this instead (Flow 10: editing must not affect the confirmed reminder until confirmed).
    var reminderPickerDate by rememberSaveable { mutableStateOf("") }
    var reminderPickerTime by rememberSaveable { mutableStateOf("") }

    var adventureType by rememberSaveable { mutableStateOf(TripAdventureType.Solo) }
    var petsIncluded by rememberSaveable { mutableStateOf(false) }
    var travelerName by rememberSaveable { mutableStateOf<String?>(null) }

    var trips by rememberSaveable(stateSaver = CompletedTripListSaver) {
        mutableStateOf(emptyList<CompletedTrip>())
    }
    var nextTripId by rememberSaveable { mutableStateOf(1L) }
    var selectedTripId by rememberSaveable { mutableStateOf<Long?>(null) }
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
    var selectedChecklistId by rememberSaveable { mutableStateOf<Long?>(null) }

    // CHECKLIST_DUPLICATE_CONFIRMATION state: the id of the just-created copy (so the confirmation
    // screen and its "Use now" both know which checklist to show), and which screen "Go back"
    // returns to -- CHECKLIST_DETAIL or CHECKLIST_EDIT, whichever hosted the "Duplicate checklist"
    // action that led here (docs/flows.md Flow 9 step 4). selectedChecklistId itself is left
    // pointing at the *source* checklist throughout, so "Go back" lands on the same
    // detail/editor context the user started from.
    var duplicatedChecklistId by rememberSaveable { mutableStateOf<Long?>(null) }
    var duplicateReturnScreen by rememberSaveable { mutableStateOf(AppScreen.ChecklistDetail) }

    // CHECKLIST_EDIT draft: a separate hoisted draft from checklistDraft above so an in-progress
    // CHECKLIST_CREATE draft is never clobbered by opening Edit on a saved checklist. Unlike
    // checklistDraft, this one is always re-seeded from the saved checklist when Edit opens (see
    // AppScreen.ChecklistDetail's onEditChecklist below), which is what makes "Back discards
    // unsaved edits" (Flow 8) true: nothing typed here reaches `checklists` unless Save is pressed,
    // and reopening Edit always starts fresh from the saved state rather than resuming a discarded
    // in-memory edit.
    var checklistEditDraft by rememberSaveable(stateSaver = ChecklistDraftSaver) {
        mutableStateOf(ChecklistDraft())
    }
    var editNewSectionName by rememberSaveable { mutableStateOf("") }
    var editNewItemName by rememberSaveable { mutableStateOf("") }
    var editNewItemSectionId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editNewItemIncludeFile by rememberSaveable { mutableStateOf(false) }

    fun resetTripDraft() {
        destinationCountry = ""
        departureCountry = ""
        tripName = ""
        oneWay = false
        departureDate = ""
        returnDate = ""
        reminderEnabled = false
        reminderDate = ""
        reminderTime = ""
        reminderPickerDate = ""
        reminderPickerTime = ""
        adventureType = TripAdventureType.Solo
        petsIncluded = false
    }

    val startNewTripDraft = {
        resetTripDraft()
        screen = AppScreen.TripDestination
    }

    // Shared by CHECKLIST_DETAIL and CHECKLIST_EDIT's "Duplicate checklist" actions (docs/flows.md
    // Flow 9): always duplicates the saved [source], never an in-progress unsaved editor draft, and
    // reuses the same nextSectionId/nextItemId counters the editors use for their own new
    // sections/items so the copy's ids can never collide with anything assigned later.
    val duplicateChecklist = { source: CompletedChecklist, returnScreen: AppScreen ->
        val duplicated = source.duplicate(
            id = nextChecklistId,
            nextSectionId = { val id = nextSectionId; nextSectionId += 1; id },
            nextItemId = { val id = nextItemId; nextItemId += 1; id }
        )
        nextChecklistId += 1
        checklists = checklists + duplicated
        duplicatedChecklistId = duplicated.id
        duplicateReturnScreen = returnScreen
        screen = AppScreen.ChecklistDuplicateConfirmation
    }

    // Documents has no approved standalone screen yet. Events opens the approved calendar sheet.
    val onAppDestinationSelected: (AppDestination) -> Unit = { destination ->
        when (destination) {
            AppDestination.Home -> screen = AppScreen.Home
            AppDestination.Checklists -> screen = AppScreen.Checklists
            AppDestination.Documents -> {}
            AppDestination.Events -> screen = AppScreen.EventsCalendar
        }
    }

    when (screen) {
        AppScreen.Home -> {
            if (trips.isEmpty()) {
                HomeEmptyScreen(
                    onAddTrip = startNewTripDraft,
                    onQuickAdd = startNewTripDraft,
                    onDestinationSelected = onAppDestinationSelected,
                    onAccountClick = { screen = AppScreen.AccountProfile }
                )
            } else {
                MyTripsScreen(
                    trips = trips,
                    filter = tripFilter,
                    onFilterChange = { tripFilter = it },
                    onAddTrip = startNewTripDraft,
                    onQuickAdd = startNewTripDraft,
                    onDestinationSelected = onAppDestinationSelected,
                    onTripSelected = {
                        selectedTripId = it
                        screen = AppScreen.TripHub
                    },
                    onAccountClick = { screen = AppScreen.AccountProfile }
                )
            }
        }

        AppScreen.AccountProfile -> {
            AccountProfileScreen(
                account = personalAccount,
                onBack = { screen = AppScreen.Home },
                onSave = {
                    personalAccount = it
                    screen = AppScreen.Home
                },
                onManageFamily = { screen = AppScreen.FamilyMembers },
                onManageInterests = { screen = AppScreen.Interests },
                onManagePrivacy = { screen = AppScreen.PrivacyConsent }
            )
        }

        AppScreen.FamilyMembers -> {
            FamilyMembersScreen(
                account = personalAccount,
                onAccountChange = { personalAccount = it },
                onBack = { screen = AppScreen.AccountProfile }
            )
        }

        AppScreen.Interests -> {
            InterestsScreen(
                account = personalAccount,
                onAccountChange = { personalAccount = it },
                onBack = { screen = AppScreen.AccountProfile }
            )
        }

        AppScreen.PrivacyConsent -> {
            PrivacyConsentScreen(
                account = personalAccount,
                onAccountChange = { personalAccount = it },
                onBack = { screen = AppScreen.AccountProfile }
            )
        }

        AppScreen.TripHub -> {
            val back = { screen = AppScreen.Home }
            BackHandler(onBack = back)
            TripHubScreen(
                trip = trips.first { it.id == selectedTripId },
                onBack = back,
                onCreateChecklist = { screen = AppScreen.ChecklistCreate }
            )
        }

        AppScreen.EventsCalendar -> {
            val back = { screen = AppScreen.Home }
            BackHandler(onBack = back)
            EventCalendarScreen(
                onBack = back,
                onSave = { screen = AppScreen.Home }
            )
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
                    onChecklistSelected = { checklistId ->
                        selectedChecklistId = checklistId
                        screen = AppScreen.ChecklistDetail
                    },
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
                onSectionNameChange = { sectionId, name ->
                    checklistDraft = checklistDraft.withSectionRenamed(sectionId, name)
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
                onItemNameChange = { itemId, name -> checklistDraft = checklistDraft.withItemNameChanged(itemId, name) },
                onItemSectionChange = { itemId, sectionId ->
                    checklistDraft = checklistDraft.withItemSectionChanged(itemId, sectionId)
                },
                onItemIncludeFileChange = { itemId, includeFile ->
                    checklistDraft = checklistDraft.withItemIncludeFileChanged(itemId, includeFile)
                },
                onRemoveItem = { itemId -> checklistDraft = checklistDraft.withItemRemoved(itemId) },
                onBack = navigateToChecklists,
                onSave = {
                    val savedDraft = checklistDraft
                    onChecklistCreated(savedDraft)
                    checklists = checklists + savedDraft.toSavedChecklist(id = nextChecklistId)
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

        AppScreen.ChecklistDetail -> {
            val navigateToChecklists = { screen = AppScreen.Checklists }
            BackHandler(onBack = navigateToChecklists)
            // CHECKLIST_DETAIL is only reached via a CHECKLISTS_POPULATED row tap or a duplicate's
            // "Use now", both of which always set selectedChecklistId to a checklist that currently
            // exists (this app has no delete, out of scope), so this lookup always resolves.
            val selectedChecklist = checklists.first { it.id == selectedChecklistId }
            ChecklistDetailScreen(
                checklist = selectedChecklist,
                onToggleItem = { itemId ->
                    checklists = checklists.map { checklist ->
                        if (checklist.id == selectedChecklist.id) {
                            checklist.withItemCompletionToggled(itemId)
                        } else {
                            checklist
                        }
                    }
                },
                onEditChecklist = {
                    checklistEditDraft = selectedChecklist.toDraft()
                    editNewSectionName = ""
                    editNewItemName = ""
                    editNewItemSectionId = null
                    editNewItemIncludeFile = false
                    screen = AppScreen.ChecklistEdit
                },
                onDuplicateChecklist = { duplicateChecklist(selectedChecklist, AppScreen.ChecklistDetail) },
                onBack = navigateToChecklists,
                onShareChecklist = { screen = AppScreen.ShareChecklist }
            )
        }

        AppScreen.ShareChecklist -> {
            val back = { screen = AppScreen.ChecklistDetail }
            BackHandler(onBack = back)
            ShareChecklistScreen(onBack = back, onFindOutMore = back)
        }

        AppScreen.ChecklistEdit -> {
            val navigateToDetail = { screen = AppScreen.ChecklistDetail }
            // Back discards unsaved edits (Flow 8): checklistEditDraft is only written back into
            // `checklists` in onSave below, and the next "Edit checklist" tap always re-seeds it
            // from the saved checklist (see onEditChecklist above), so nothing typed after this
            // Back press is ever persisted.
            BackHandler(onBack = navigateToDetail)
            // CHECKLIST_EDIT is only reached via a CHECKLIST_DETAIL "Edit checklist" tap, which
            // always leaves selectedChecklistId set to a checklist that currently exists (this app
            // has no delete, out of scope), so this lookup always resolves -- same assumption as
            // AppScreen.ChecklistDetail above.
            val editedChecklistId = requireNotNull(selectedChecklistId)
            ChecklistCreateScreen(
                draft = checklistEditDraft,
                title = stringResource(R.string.checklist_edit_title),
                onNameChange = { checklistEditDraft = checklistEditDraft.copy(name = it) },
                newSectionName = editNewSectionName,
                onNewSectionNameChange = { editNewSectionName = it },
                onAddSection = {
                    if (editNewSectionName.isNotBlank()) {
                        checklistEditDraft = checklistEditDraft.withSectionAdded(
                            ChecklistSection(id = nextSectionId, name = editNewSectionName.trim())
                        )
                        nextSectionId += 1
                        editNewSectionName = ""
                    }
                },
                onSectionNameChange = { sectionId, name ->
                    checklistEditDraft = checklistEditDraft.withSectionRenamed(sectionId, name)
                },
                onRemoveSection = { sectionId ->
                    checklistEditDraft = checklistEditDraft.withSectionRemoved(sectionId)
                    if (editNewItemSectionId == sectionId) editNewItemSectionId = null
                },
                newItemName = editNewItemName,
                onNewItemNameChange = { editNewItemName = it },
                newItemSectionId = editNewItemSectionId,
                onNewItemSectionIdChange = { editNewItemSectionId = it },
                newItemIncludeFile = editNewItemIncludeFile,
                onNewItemIncludeFileChange = { editNewItemIncludeFile = it },
                onAddItem = {
                    if (editNewItemName.isNotBlank()) {
                        checklistEditDraft = checklistEditDraft.withItemAdded(
                            ChecklistItemDraft(
                                id = nextItemId,
                                name = editNewItemName.trim(),
                                sectionId = editNewItemSectionId,
                                includeFile = editNewItemIncludeFile
                            )
                        )
                        nextItemId += 1
                        editNewItemName = ""
                        editNewItemSectionId = null
                        editNewItemIncludeFile = false
                    }
                },
                onItemNameChange = { itemId, name ->
                    checklistEditDraft = checklistEditDraft.withItemNameChanged(itemId, name)
                },
                onItemSectionChange = { itemId, sectionId ->
                    checklistEditDraft = checklistEditDraft.withItemSectionChanged(itemId, sectionId)
                },
                onItemIncludeFileChange = { itemId, includeFile ->
                    checklistEditDraft = checklistEditDraft.withItemIncludeFileChanged(itemId, includeFile)
                },
                onRemoveItem = { itemId -> checklistEditDraft = checklistEditDraft.withItemRemoved(itemId) },
                onBack = navigateToDetail,
                onSave = {
                    val savedDraft = checklistEditDraft
                    checklists = checklists.map { checklist ->
                        if (checklist.id == editedChecklistId) checklist.withDraftApplied(savedDraft) else checklist
                    }
                    screen = AppScreen.ChecklistDetail
                },
                // Duplicates the saved checklist as it currently exists, not this screen's unsaved
                // in-progress edits -- consistent with "Back discards unsaved edits" above: nothing
                // typed in this editor is visible anywhere else until Save is pressed.
                onDuplicateChecklist = {
                    val savedChecklist = checklists.first { it.id == editedChecklistId }
                    duplicateChecklist(savedChecklist, AppScreen.ChecklistEdit)
                }
            )
        }

        AppScreen.ChecklistDuplicateConfirmation -> {
            val goBackToSource = { screen = duplicateReturnScreen }
            BackHandler(onBack = goBackToSource)
            // Only reachable right after duplicateChecklist() set duplicatedChecklistId to a
            // checklist it just appended to `checklists`, so this lookup always resolves.
            val duplicatedChecklist = checklists.first { it.id == duplicatedChecklistId }
            ChecklistDuplicateConfirmationScreen(
                checklistName = duplicatedChecklist.name,
                onGoBack = goBackToSource,
                onUseNow = {
                    selectedChecklistId = duplicatedChecklist.id
                    screen = AppScreen.ChecklistDetail
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
                reminderSummary = if (reminderEnabled) {
                    stringResource(R.string.trip_reminder_summary_format, reminderDate, reminderTime)
                } else {
                    null
                },
                onOpenReminderPicker = {
                    // Seed REMINDER_PICKER from the last confirmed value (blank if none yet), so
                    // both "turn on for the first time" and "edit an existing reminder" pre-fill
                    // consistently (docs/flows.md Flow 10).
                    reminderPickerDate = reminderDate
                    reminderPickerTime = reminderTime
                    screen = AppScreen.ReminderPicker
                },
                // Turning the control off disables the reminder immediately -- no REMINDER_PICKER
                // round trip needed, and reminderDate/reminderTime are left untouched so turning it
                // back on (or reopening the picker) still pre-fills the same values.
                onReminderDisabled = { reminderEnabled = false },
                onLoadTicket = {},
                onBack = navigateToDestination,
                onNextStep = { screen = AppScreen.TripTravelers }
            )
        }

        AppScreen.ReminderPicker -> {
            val navigateToDates = { screen = AppScreen.TripDates }
            // Back discards unsaved edits (Flow 10): reminderPickerDate/Time are never written back
            // to reminderDate/reminderTime/reminderEnabled except by onConfirm below, so the trip
            // draft (including any previously confirmed reminder) is untouched.
            BackHandler(onBack = navigateToDates)
            ReminderPickerScreen(
                date = reminderPickerDate,
                onDateChange = { reminderPickerDate = it },
                time = reminderPickerTime,
                onTimeChange = { reminderPickerTime = it },
                onBack = navigateToDates,
                onConfirm = {
                    reminderDate = reminderPickerDate
                    reminderTime = reminderPickerTime
                    reminderEnabled = true
                    screen = AppScreen.TripDates
                }
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
                onAddTraveler = { screen = AppScreen.MyTravelers },
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
                        reminderEnabled = reminderEnabled,
                        reminderDate = reminderDate,
                        reminderTime = reminderTime
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

        AppScreen.MyTravelers -> {
            val back = { screen = AppScreen.TripTravelers }
            BackHandler(onBack = back)
            MyTravelersScreen(
                name = travelerName,
                onBack = back,
                onAdd = { screen = AppScreen.TravelerEditor },
                onEdit = { screen = AppScreen.TravelerEditor }
            )
        }

        AppScreen.TravelerEditor -> {
            val back = { screen = AppScreen.MyTravelers }
            BackHandler(onBack = back)
            TravelerEditorScreen(
                initialName = travelerName.orEmpty(),
                onBack = back,
                onSave = {
                    travelerName = it
                    screen = AppScreen.MyTravelers
                }
            )
        }
    }
}
