package co.check2go.feature.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.feature.trip.CompletedTrip
import co.check2go.feature.trip.TripFilter
import co.check2go.ui.theme.Check2GoTheme

/**
 * Shared screen ID: HOME_TRIPS ("My trips — list view").
 *
 * Stateless representation: filtering, trip data and navigation are all supplied by the caller.
 * Assumes [trips] is non-empty; the caller shows [HomeEmptyScreen] instead while there are no
 * completed trips yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    trips: List<CompletedTrip>,
    filter: TripFilter,
    onFilterChange: (TripFilter) -> Unit,
    onAddTrip: () -> Unit,
    onQuickAdd: () -> Unit,
    onDestinationSelected: (HomeDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAddLabel = stringResource(R.string.home_quick_add)

    // No archiving/cancellation state exists yet (see docs/screen-inventory.md), so every
    // completed trip is currently "active" and both tabs show the same list. The controls are
    // wired up now so filtering can be layered on without a screen rework later.
    val filteredTrips = trips

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.trip_my_trips_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            HomeNavigationBar(onDestinationSelected = onDestinationSelected)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQuickAdd,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.semantics {
                    contentDescription = quickAddLabel
                    role = Role.Button
                }
            ) {
                Text(text = "+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = filter == TripFilter.Active,
                    onClick = { onFilterChange(TripFilter.Active) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(text = stringResource(R.string.trip_filter_active))
                }
                SegmentedButton(
                    selected = filter == TripFilter.All,
                    onClick = { onFilterChange(TripFilter.All) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(text = stringResource(R.string.trip_filter_all))
                }
            }
            Button(onClick = onAddTrip, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.home_add_trip))
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredTrips.forEach { trip ->
                    TripCard(trip = trip)
                }
            }
        }
    }
}

@Composable
private fun TripCard(trip: CompletedTrip, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TripCardImagePlaceholder()
        Text(
            text = trip.tripName,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(
                R.string.trip_route_format,
                trip.departureCountry,
                trip.destinationCountry
            ),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = trip.datesLabel(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        // Neutral, non-date-derived status: no archiving/upcoming/past business rule exists yet.
        Text(
            text = stringResource(R.string.trip_status_added),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun CompletedTrip.datesLabel(): String = when {
    departureDate.isBlank() && (oneWay || returnDate.isBlank()) ->
        stringResource(R.string.trip_dates_not_set)
    oneWay -> departureDate
    returnDate.isBlank() -> departureDate
    else -> stringResource(R.string.trip_dates_round_trip_format, departureDate, returnDate)
}

@Composable
private fun TripCardImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .size(96.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.trip_card_image_placeholder),
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTripsScreenPreview() {
    Check2GoTheme(darkTheme = false) {
        MyTripsScreen(
            trips = listOf(
                CompletedTrip(
                    id = 1L,
                    tripName = "Summer trip",
                    destinationCountry = "Italy",
                    departureCountry = "Israel",
                    oneWay = false,
                    departureDate = "2026-10-01",
                    returnDate = "2026-10-10"
                )
            ),
            filter = TripFilter.Active,
            onFilterChange = {},
            onAddTrip = {},
            onQuickAdd = {},
            onDestinationSelected = {}
        )
    }
}
