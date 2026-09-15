package co.check2go.feature.trip

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

data class TripDestinationDraft(
    val destinationCountry: String,
    val departureCountry: String,
    val tripName: String
)

/** Stateless representation of the shared TRIP_CREATE_DESTINATION screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCreateDestinationScreen(
    destinationCountry: String,
    onDestinationCountryChange: (String) -> Unit,
    departureCountry: String,
    onDepartureCountryChange: (String) -> Unit,
    tripName: String,
    onTripNameChange: (String) -> Unit,
    onAutofill: () -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit,
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
            TravelImagePlaceholder(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = stringResource(R.string.trip_create_destination_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                value = destinationCountry,
                onValueChange = onDestinationCountryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.trip_destination_country)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onAutofill) {
                    Text(text = stringResource(R.string.trip_autofill))
                }
            }
            Text(
                text = stringResource(R.string.trip_autofill_description),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End
            )
            OutlinedTextField(
                value = departureCountry,
                onValueChange = onDepartureCountryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.trip_departure_country)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = tripName,
                onValueChange = onTripNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.trip_name)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(text = stringResource(R.string.trip_start))
            }
        }
    }
}

@Composable
private fun TravelImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 208.dp, height = 112.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.trip_image_placeholder),
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCreateDestinationLightPreview() {
    Check2GoTheme(darkTheme = false) {
        TripCreateDestinationScreen("", {}, "", {}, "", {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCreateDestinationDarkPreview() {
    Check2GoTheme(darkTheme = true) {
        TripCreateDestinationScreen("Italy", {}, "Israel", {}, "Summer trip", {}, {}, {}, {})
    }
}
