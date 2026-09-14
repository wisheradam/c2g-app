package co.check2go.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import co.check2go.feature.home.HomeEmptyScreen
import co.check2go.feature.trip.TripCreateDestinationScreen
import co.check2go.feature.trip.TripDestinationDraft

/** Minimal app-level navigation for HOME_EMPTY -> TRIP_CREATE_DESTINATION. */
@Composable
fun Check2GoApp(
    onTripStart: (TripDestinationDraft) -> Unit
) {
    var showTripCreate by rememberSaveable { mutableStateOf(false) }
    var destinationCountry by rememberSaveable { mutableStateOf("") }
    var departureCountry by rememberSaveable { mutableStateOf("") }
    var tripName by rememberSaveable { mutableStateOf("") }

    if (showTripCreate) {
        val navigateHome = { showTripCreate = false }
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
            onStart = {
                onTripStart(
                    TripDestinationDraft(
                        destinationCountry = destinationCountry,
                        departureCountry = departureCountry,
                        tripName = tripName
                    )
                )
            }
        )
    } else {
        HomeEmptyScreen(
            onAddTrip = { showTripCreate = true },
            onQuickAdd = { showTripCreate = true },
            onDestinationSelected = {}
        )
    }
}
