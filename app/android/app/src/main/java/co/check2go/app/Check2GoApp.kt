package co.check2go.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

private enum class AppScreen { Home, TripDestination, TripDates, TripTravelers }

/** Minimal app-level navigation for HOME_EMPTY/HOME_TRIPS -> TRIP_CREATE_DESTINATION -> TRIP_CREATE_DATES -> TRIP_CREATE_TRAVELERS. */
@Composable
fun Check2GoApp(
    onTripCreateComplete: (TripDestinationDraft, TripDatesDraft, TripTravelersDraft) -> Unit
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

    when (screen) {
        AppScreen.Home -> {
            if (trips.isEmpty()) {
                HomeEmptyScreen(
                    onAddTrip = startNewTripDraft,
                    onQuickAdd = startNewTripDraft,
                    onDestinationSelected = {}
                )
            } else {
                MyTripsScreen(
                    trips = trips,
                    filter = tripFilter,
                    onFilterChange = { tripFilter = it },
                    onAddTrip = startNewTripDraft,
                    onQuickAdd = startNewTripDraft,
                    onDestinationSelected = {}
                )
            }
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
