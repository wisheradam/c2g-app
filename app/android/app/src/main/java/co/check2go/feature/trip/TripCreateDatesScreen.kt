package co.check2go.feature.trip

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/**
 * Draft captured by the shared TRIP_CREATE_DATES screen. [reminderDate]/[reminderTime] hold the
 * last REMINDER_PICKER value confirmed via "Set a reminder" (docs/flows.md Flow 10); they are
 * meaningful only while [reminderEnabled] is true, but are preserved (not cleared) when the
 * reminder is disabled so re-enabling it pre-fills the same values.
 */
data class TripDatesDraft(
    val oneWay: Boolean,
    val departureDate: String,
    val returnDate: String,
    val reminderEnabled: Boolean,
    val reminderDate: String = "",
    val reminderTime: String = ""
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
    reminderSummary: String?,
    onOpenReminderPicker: () -> Unit,
    onReminderDisabled: () -> Unit,
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Turning the control on navigates to REMINDER_PICKER rather than enabling
                        // the reminder directly (Flow 10, steps 1-5: the reminder is only confirmed
                        // once "Set a reminder" is pressed there). Turning it off disables the
                        // reminder immediately -- coherent "remove" behavior for a control that has
                        // no real OS notification to cancel.
                        .toggleable(
                            value = reminderEnabled,
                            onValueChange = { checked ->
                                if (checked) onOpenReminderPicker() else onReminderDisabled()
                            },
                            role = Role.Switch
                        )
                        .testTag("trip_reminder_toggle"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.trip_reminder_label),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleSmall
                    )
                    // Switch itself is non-interactive; the enclosing Row's toggleable() drives it
                    // so the label and control share one accessible tap target and announcement.
                    Switch(checked = reminderEnabled, onCheckedChange = null)
                }
                if (reminderEnabled && reminderSummary != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reminderSummary,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = onOpenReminderPicker) {
                            Text(text = stringResource(R.string.trip_reminder_edit))
                        }
                    }
                }
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
            reminderSummary = null,
            onOpenReminderPicker = {},
            onReminderDisabled = {},
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
            reminderSummary = "Reminder: 2026-09-30 at 09:00",
            onOpenReminderPicker = {},
            onReminderDisabled = {},
            onLoadTicket = {},
            onBack = {},
            onNextStep = {}
        )
    }
}
