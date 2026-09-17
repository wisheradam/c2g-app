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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

data class ResidencePermitDraft(
    val countryCode: String = "", val status: ResidencyStatus = ResidencyStatus.VisaHolder,
    val permitType: String = "", val validFrom: String = "", val validUntil: String = ""
) {
    fun validationError(): String? = when {
        countryCode.isBlank() -> "Country code is required"
        invalidDate(validFrom) || invalidDate(validUntil) -> "Use date format YYYY-MM-DD"
        else -> null
    }

    fun toPermit(id: String) = ResidencePermit(
        id, countryCode.trim().uppercase(), status, permitType.trim().ifBlank { null },
        parseDateOrNull(validFrom), parseDateOrNull(validUntil)
    )
}

data class EntryRestrictionDraft(
    val countryCode: String = "", val type: EntryRestrictionType = EntryRestrictionType.EntryRestriction,
    val details: String = "", val effectiveFrom: String = "", val effectiveUntil: String = ""
) {
    fun validationError(): String? = when {
        countryCode.isBlank() -> "Country code is required"
        details.isBlank() -> "Details are required"
        invalidDate(effectiveFrom) || invalidDate(effectiveUntil) -> "Use date format YYYY-MM-DD"
        else -> null
    }

    fun toRestriction(id: String) = EntryRestriction(
        id, countryCode.trim().uppercase(), type, details.trim(),
        parseDateOrNull(effectiveFrom), parseDateOrNull(effectiveUntil)
    )
}

private fun invalidDate(value: String) = value.isNotBlank() && runCatching { LocalDate.parse(value.trim()) }.isFailure
private fun parseDateOrNull(value: String): LocalDate? = value.takeIf(String::isNotBlank)?.trim()?.let(LocalDate::parse)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelStatusScreen(account: PersonalAccount, onAccountChange: (PersonalAccount) -> Unit, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    var permitDraft by remember { mutableStateOf(ResidencePermitDraft()) }
    var restrictionDraft by remember { mutableStateOf(EntryRestrictionDraft()) }
    var permitError by remember { mutableStateOf<String?>(null) }
    var restrictionError by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Travel status") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Residence permits", style = MaterialTheme.typography.titleLarge) }
            items(account.residencePermits, key = { it.id }) { permit ->
                StatusCard("${permit.countryCode} · ${permit.status.name}", permit.permitType.orEmpty()) {
                    onAccountChange(account.copy(residencePermits = account.residencePermits.filterNot { it.id == permit.id }))
                }
            }
            item { StatusField("Country code", permitDraft.countryCode) { permitDraft = permitDraft.copy(countryCode = it) } }
            item { StatusEnum("Residency status", permitDraft.status, ResidencyStatus.entries) { permitDraft = permitDraft.copy(status = it) } }
            item { StatusField("Permit type", permitDraft.permitType) { permitDraft = permitDraft.copy(permitType = it) } }
            item { StatusField("Valid from (YYYY-MM-DD)", permitDraft.validFrom) { permitDraft = permitDraft.copy(validFrom = it) } }
            item { StatusField("Valid until (YYYY-MM-DD)", permitDraft.validUntil) { permitDraft = permitDraft.copy(validUntil = it) } }
            item { permitError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item {
                Button(onClick = {
                    permitError = permitDraft.validationError()
                    if (permitError == null) {
                        onAccountChange(account.copy(residencePermits = account.residencePermits + permitDraft.toPermit("permit-${System.nanoTime()}")))
                        permitDraft = ResidencePermitDraft()
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("Add residence permit") }
            }

            item { Text("Entry restrictions and travel bans", style = MaterialTheme.typography.titleLarge) }
            items(account.entryRestrictions, key = { it.id }) { restriction ->
                StatusCard("${restriction.countryCode} · ${restriction.type.name}", restriction.details) {
                    onAccountChange(account.copy(entryRestrictions = account.entryRestrictions.filterNot { it.id == restriction.id }))
                }
            }
            item { StatusField("Restricted country code", restrictionDraft.countryCode) { restrictionDraft = restrictionDraft.copy(countryCode = it) } }
            item { StatusEnum("Restriction type", restrictionDraft.type, EntryRestrictionType.entries) { restrictionDraft = restrictionDraft.copy(type = it) } }
            item { StatusField("Details", restrictionDraft.details) { restrictionDraft = restrictionDraft.copy(details = it) } }
            item { StatusField("Effective from (YYYY-MM-DD)", restrictionDraft.effectiveFrom) { restrictionDraft = restrictionDraft.copy(effectiveFrom = it) } }
            item { StatusField("Effective until (YYYY-MM-DD)", restrictionDraft.effectiveUntil) { restrictionDraft = restrictionDraft.copy(effectiveUntil = it) } }
            item { restrictionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item {
                Button(onClick = {
                    restrictionError = restrictionDraft.validationError()
                    if (restrictionError == null) {
                        onAccountChange(account.copy(entryRestrictions = account.entryRestrictions + restrictionDraft.toRestriction("restriction-${System.nanoTime()}")))
                        restrictionDraft = EntryRestrictionDraft()
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("Add restriction") }
            }
        }
    }
}

@Composable
private fun StatusCard(title: String, detail: String, onRemove: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) { Text(title); if (detail.isNotBlank()) Text(detail) }
            TextButton(onClick = onRemove) { Text("Remove") }
        }
    }
}

@Composable
private fun StatusField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Enum<T>> StatusEnum(label: String, value: T, values: List<T>, onChange: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }) {
        OutlinedTextField(value.name, {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
        ExposedDropdownMenu(expanded, { expanded = false }) {
            values.forEach { option -> DropdownMenuItem({ Text(option.name) }, { onChange(option); expanded = false }) }
        }
    }
}
