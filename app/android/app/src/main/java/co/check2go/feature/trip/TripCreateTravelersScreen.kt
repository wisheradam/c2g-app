package co.check2go.feature.trip

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/** Whether the trip is being planned solo or with a group of travelers. */
enum class TripAdventureType { Solo, Group }

/** Draft captured by the shared TRIP_CREATE_TRAVELERS screen. */
data class TripTravelersDraft(
    val adventureType: TripAdventureType,
    val petsIncluded: Boolean
)

/** Stateless representation of the shared TRIP_CREATE_TRAVELERS screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCreateTravelersScreen(
    adventureType: TripAdventureType,
    onAdventureTypeChange: (TripAdventureType) -> Unit,
    petsIncluded: Boolean,
    onPetsIncludedChange: (Boolean) -> Unit,
    onAddTraveler: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.trip_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
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
            TripCreateStepIndicator(currentStep = 3)
            Text(
                text = stringResource(R.string.trip_create_travelers_title),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = adventureType == TripAdventureType.Solo,
                    onClick = { onAdventureTypeChange(TripAdventureType.Solo) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(text = stringResource(R.string.trip_solo_adventure))
                }
                SegmentedButton(
                    selected = adventureType == TripAdventureType.Group,
                    onClick = { onAdventureTypeChange(TripAdventureType.Group) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(text = stringResource(R.string.trip_group_adventure))
                }
            }
            if (adventureType == TripAdventureType.Group) {
                MyTravelersPanel(onAddTraveler = onAddTraveler)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.trip_pets_label),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !petsIncluded,
                        onClick = { onPetsIncludedChange(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(text = stringResource(R.string.trip_pets_no))
                    }
                    SegmentedButton(
                        selected = petsIncluded,
                        onClick = { onPetsIncludedChange(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(text = stringResource(R.string.trip_pets_yes))
                    }
                }
            }
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(text = stringResource(R.string.trip_complete))
            }
        }
    }
}

/**
 * Clearly temporary group-mode panel. No traveler editing, profile persistence, fake
 * profiles or backend are wired up yet; this only exposes the entry point for it.
 */
@Composable
private fun MyTravelersPanel(onAddTraveler: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.trip_my_travelers_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.trip_my_travelers_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        OutlinedButton(onClick = onAddTraveler, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.trip_add_traveler))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCreateTravelersSoloPreview() {
    Check2GoTheme(darkTheme = false) {
        TripCreateTravelersScreen(
            adventureType = TripAdventureType.Solo,
            onAdventureTypeChange = {},
            petsIncluded = false,
            onPetsIncludedChange = {},
            onAddTraveler = {},
            onBack = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCreateTravelersGroupPreview() {
    Check2GoTheme(darkTheme = true) {
        TripCreateTravelersScreen(
            adventureType = TripAdventureType.Group,
            onAdventureTypeChange = {},
            petsIncluded = true,
            onPetsIncludedChange = {},
            onAddTraveler = {},
            onBack = {},
            onComplete = {}
        )
    }
}
