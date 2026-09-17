package co.check2go.feature.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/**
 * Stateless representation of the shared REMINDER_PICKER screen (docs/flows.md Flow 10;
 * docs/screen-inventory.md "Reminder / Date-Time Picker"). [date]/[time] are the same clearly
 * temporary ISO-8601/24-hour text input as [TripCreateDatesScreen]'s departure/return dates: no
 * calendar/time picker control has been recovered from Figma yet, so free-text entry stands in for
 * it, validated by [ReminderDraft.isValid].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPickerScreen(
    date: String,
    onDateChange: (String) -> Unit,
    time: String,
    onTimeChange: (String) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val draft = ReminderDraft(date = date, time = time)

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
            Text(
                text = stringResource(R.string.reminder_picker_title),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reminder_date_field"),
                label = { Text(stringResource(R.string.reminder_date_label)) },
                placeholder = { Text(stringResource(R.string.trip_date_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = time,
                onValueChange = onTimeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reminder_time_field"),
                label = { Text(stringResource(R.string.reminder_time_label)) },
                placeholder = { Text(stringResource(R.string.reminder_time_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Text(
                text = stringResource(R.string.reminder_field_helper),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = onConfirm,
                enabled = draft.isValid(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("reminder_set_action")
            ) {
                Text(text = stringResource(R.string.reminder_set_action))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderPickerEmptyPreview() {
    Check2GoTheme(darkTheme = false) {
        ReminderPickerScreen(
            date = "",
            onDateChange = {},
            time = "",
            onTimeChange = {},
            onBack = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderPickerFilledPreview() {
    Check2GoTheme(darkTheme = true) {
        ReminderPickerScreen(
            date = "2026-10-01",
            onDateChange = {},
            time = "09:30",
            onTimeChange = {},
            onBack = {},
            onConfirm = {}
        )
    }
}
