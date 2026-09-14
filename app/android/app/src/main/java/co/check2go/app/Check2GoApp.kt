package co.check2go.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import co.check2go.feature.home.HomeEmptyScreen
import co.check2go.feature.trip.TripCreateDatesScreen
import co.check2go.feature.trip.TripCreateDestinationScreen
import co.check2go.feature.trip.TripDatesDraft
import co.check2go.feature.trip.TripDestinationDraft

private enum class AppScreen { Home, TripDestination, TripDates }

/** Minimal app-level navigation for HOME_EMPTY -> TRIP_CREATE_DESTINATION -> TRIP_CREATE_DATES. */
@Composable
fun Check2GoApp(
    onTripDatesNext: (TripDestinationDraft, TripDatesDraft) -> Unit
) {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Home) }

    var destinationCountry by rememberSaveable { mutableStateOf("") }
    var departureCountry by rememberSaveable { mutableStateOf("") }
    var tripName by rememberSaveable { mutableStateOf("") }

    var oneWay by rememberSaveable { mutableStateOf(false) }
    var departureDate by rememberSaveable { mutableStateOf("") }
    var returnDate by rememberSaveable { mutableStateOf("") }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }

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
                onNextStep = {
                    onTripDatesNext(
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
                        )
                    )
                }
            )
        }
    }
}
