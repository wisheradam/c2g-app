package co.check2go.feature.trip

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/** Draft captured by the shared TRIP_CREATE_DATES screen. */
data class TripDatesDraft(
    val oneWay: Boolean,
    val departureDate: String,
    val returnDate: String,
    val reminderEnabled: Boolean
)

/** Stateless representation of the shared TRIP_CREATE_DATES screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCreateDatesScreen(
    oneWay: Boolean,
    onOneWayChange: (Boolean) -> Unit,
    departureDate: String,
    onDepartureDateChange: (String) -> Unit,
    returnDate: String,
    onReturnDateChange: (String) -> Unit,
    reminderEnabled: Boolean,
    onReminderEnabledChange: (Boolean) -> Unit,
    onLoadTicket: () -> Unit,
    onBack: () -> Unit,
    onNextStep: () -> Unit,
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
            TripCreateStepIndicator(currentStep = 2)
            Text(
                text = stringResource(R.string.trip_create_dates_title),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            QuickFillPanel(onLoadTicket = onLoadTicket)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.trip_one_way_label),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !oneWay,
                        onClick = { onOneWayChange(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(text = stringResource(R.string.trip_one_way_no))
                    }
                    SegmentedButton(
                        selected = oneWay,
                        onClick = { onOneWayChange(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(text = stringResource(R.string.trip_one_way_yes))
                    }
                }
            }
            OutlinedTextField(
                value = departureDate,
                onValueChange = onDepartureDateChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.trip_departure_date)) },
                placeholder = { Text(stringResource(R.string.trip_date_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = if (oneWay) ImeAction.Done else ImeAction.Next
                )
            )
            if (!oneWay) {
                OutlinedTextField(
                    value = returnDate,
                    onValueChange = onReturnDateChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.trip_return_date)) },
                    placeholder = { Text(stringResource(R.string.trip_date_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
            Text(
                text = stringResource(R.string.trip_date_field_helper),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.trip_reminder_label),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )
                Switch(checked = reminderEnabled, onCheckedChange = onReminderEnabledChange)
            }
            Button(
                onClick = onNextStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(text = stringResource(R.string.trip_next_step))
            }
        }
    }
}

/**
 * Temporary step indicator until exact Figma step-indicator spacing/typography is recovered.
 * Reflects the three-step Add Trip flow: Destination -> Dates -> Travelers.
 */
@Composable
private fun TripCreateStepIndicator(currentStep: Int, modifier: Modifier = Modifier) {
    val steps = listOf(
        1 to stringResource(R.string.trip_step_destination),
        2 to stringResource(R.string.trip_step_dates),
        3 to stringResource(R.string.trip_step_travelers)
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        steps.forEach { (step, label) ->
            val isCurrent = step == currentStep
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = if (isCurrent) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.toString(),
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Text(
                    text = label,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Quick Fill panel with a clearly temporary ticket-upload action.
 * No real file picker, OCR or backend is wired up yet.
 */
@Composable
private fun QuickFillPanel(onLoadTicket: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.trip_quick_fill_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedButton(onClick = onLoadTicket, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.trip_load_ticket))
        }
        Text(
            text = stringResource(R.string.trip_load_ticket_description),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCreateDatesRoundTripPreview() {
    Check2GoTheme(darkTheme = false) {
        TripCreateDatesScreen(
            oneWay = false,
            onOneWayChange = {},
            departureDate = "",
            onDepartureDateChange = {},
            returnDate = "",
            onReturnDateChange = {},
            reminderEnabled = false,
            onReminderEnabledChange = {},
            onLoadTicket = {},
            onBack = {},
            onNextStep = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCreateDatesOneWayPreview() {
    Check2GoTheme(darkTheme = true) {
        TripCreateDatesScreen(
            oneWay = true,
            onOneWayChange = {},
            departureDate = "2026-10-01",
            onDepartureDateChange = {},
            returnDate = "",
            onReturnDateChange = {},
            reminderEnabled = true,
            onReminderEnabledChange = {},
            onLoadTicket = {},
            onBack = {},
            onNextStep = {}
        )
    }
}
