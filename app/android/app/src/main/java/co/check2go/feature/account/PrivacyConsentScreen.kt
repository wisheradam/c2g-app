package co.check2go.feature.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Clock
import java.time.Instant

private const val CURRENT_CONSENT_VERSION = "1.0"

private val consentLabels = mapOf(
    ConsentType.PersonalInformationUse to "Use and processing of personal information according to the Privacy Policy",
    ConsentType.PrivacyPolicy to "Privacy Policy accepted",
    ConsentType.TermsOfService to "Terms of Service accepted",
    ConsentType.PersonalDataProcessing to "Personal data processing",
    ConsentType.PersonalizedRecommendations to "Use profile data for personalized recommendations",
    ConsentType.FamilyMemberDataProcessing to "Process family-member data",
    ConsentType.MarketingCommunications to "Marketing communications (optional)",
    ConsentType.ProductUpdatesAndNotifications to "Product updates and notifications (optional)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyConsentScreen(
    account: PersonalAccount,
    onAccountChange: (PersonalAccount) -> Unit,
    onBack: () -> Unit,
    now: () -> Instant = Instant::now
) {
    BackHandler(onBack = onBack)
    var confirmDeletion by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Privacy and consent") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Consent version $CURRENT_CONSENT_VERSION", style = MaterialTheme.typography.titleMedium) }
            items(ConsentType.entries) { type ->
                val current = account.consents.firstOrNull { it.type == type }?.currentDecision
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(consentLabels.getValue(type))
                        current?.let { Text("${it.status.name} · ${it.recordedAt}", style = MaterialTheme.typography.bodySmall) }
                    }
                    Switch(
                        checked = current?.status == ConsentStatus.Accepted,
                        onCheckedChange = { accepted ->
                            onAccountChange(account.withConsentDecision(type, if (accepted) ConsentStatus.Accepted else ConsentStatus.Declined, CURRENT_CONSENT_VERSION, now()))
                        }
                    )
                }
            }
            item {
                val missing = account.missingRequiredConsents()
                if (missing.isNotEmpty()) Text("Required consents missing: ${missing.size}", color = MaterialTheme.colorScheme.error)
            }
            item {
                Button(onClick = { confirmDeletion = true }, modifier = Modifier.fillMaxWidth()) { Text("Request account deletion") }
            }
            account.deletionRequest?.let { request ->
                item { Text("Deletion request: ${request.status.name} · ${request.requestedAt}") }
            }
        }
    }
    if (confirmDeletion) {
        AlertDialog(
            onDismissRequest = { confirmDeletion = false },
            title = { Text("Delete account and personal data?") },
            text = { Text("This creates a deletion request for the account and associated personal data.") },
            confirmButton = {
                TextButton(onClick = {
                    onAccountChange(account.requestAccountDeletion(Clock.systemUTC()))
                    confirmDeletion = false
                }) { Text("Request deletion") }
            },
            dismissButton = { TextButton(onClick = { confirmDeletion = false }) { Text("Cancel") } }
        )
    }
}
