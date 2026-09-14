package co.check2go.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import co.check2go.feature.home.HomeEmptyScreen
import co.check2go.feature.trip.TripAdventureType
import co.check2go.feature.trip.TripCreateDatesScreen
import co.check2go.feature.trip.TripCreateDestinationScreen
import co.check2go.feature.trip.TripCreateTravelersScreen
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft
import co.check2go.feature.trip.TripTravelersDraft

private enum class AppScreen { Home, TripDestination, TripDates, TripTravelers }

/** Minimal app-level navigation for HOME_EMPTY -> TRIP_CREATE_DESTINATION -> TRIP_CREATE_DATES -> TRIP_CREATE_TRAVELERS. */
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

    when (screen) {
        AppScreen.Home -> {
            HomeEmptyScreen(
                onAddTrip = { screen = AppScreen.TripDestination },
                onQuickAdd = { screen = AppScreen.TripDestination },
                onDestinationSelected = {}
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
                    onTripCreateComplete(
                        TripDestinationDraft(
                            destinationCountry = destinationCountry,
                            departureCountry = departureCountry,
                            tripName = tripName
                        ),
                        TripDatesDraft(
                            oneWay = oneWay,
                            departureDate = departureDate,
                            returnDate = returnDate,
                            reminderEnabled = reminderEnabled
                        ),
                        TripTravelersDraft(
                            adventureType = adventureType,
                            petsIncluded = petsIncluded
                        )
                    )
                }
            )
        }
    }
}
