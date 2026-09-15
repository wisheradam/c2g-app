package co.check2go.feature.checklists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/**
 * Shared screen ID: CHECKLIST_DUPLICATE_CONFIRMATION (docs/flows.md Flow 9, steps 3-4;
 * docs/screen-inventory.md "Duplicate success bottom sheet", PDF page 41). Reached after
 * CHECKLIST_DETAIL or CHECKLIST_EDIT's "Duplicate checklist" action creates the copy.
 *
 * The current Figma/PDF renders this as a bottom sheet with specific spacing/typography that has
 * not been recovered as exact values (see docs/design/design-tokens.md's rule against inventing
 * missing dimensions). This is deliberately a plain, clearly temporary full-screen Compose surface
 * using only default Material3 styling instead of a guessed-final bottom sheet.
 *
 * Stateless: duplication itself already happened by the time this is shown, so this screen only
 * confirms it and forwards the two documented choices -- [onGoBack] (Flow 9 step 4 "Go back") and
 * [onUseNow] (Flow 9 step 4 "Use now") -- to the caller, which owns navigation.
 */
@Composable
fun ChecklistDuplicateConfirmationScreen(
    checklistName: String,
    onGoBack: () -> Unit,
    onUseNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.checklist_duplicate_confirmation_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = checklistName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = onUseNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_duplicate_use_now")
            ) {
                Text(text = stringResource(R.string.checklist_duplicate_use_now))
            }
            OutlinedButton(
                onClick = onGoBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_duplicate_go_back")
            ) {
                Text(text = stringResource(R.string.checklist_duplicate_go_back))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChecklistDuplicateConfirmationPreview() {
    Check2GoTheme(darkTheme = false) {
        ChecklistDuplicateConfirmationScreen(
            checklistName = "Before leaving (copy)",
            onGoBack = {},
            onUseNow = {}
        )
    }
}
