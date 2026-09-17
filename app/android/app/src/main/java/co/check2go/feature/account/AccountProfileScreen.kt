package co.check2go.feature.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class ProfileDraft(
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val gender: GenderOption = GenderOption.Unspecified,
    val dateOfBirth: String = "",
    val addressLine1: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: String = "",
    val addressCountryCode: String = "",
    val citizenshipCountryCodes: Set<String> = emptySet(),
    val residenceCountryCode: String = "",
    val residencePlace: String = "",
    val residencyStatus: ResidencyStatus = ResidencyStatus.Unknown
) {
    fun validationError(referenceDate: LocalDate): String? {
        if (firstName.isBlank()) return "First name is required"
        if (lastName.isBlank()) return "Last name is required"
        if (dateOfBirth.isNotBlank()) {
            val parsed = try { LocalDate.parse(dateOfBirth.trim()) } catch (_: DateTimeParseException) {
                return "Use date format YYYY-MM-DD"
            }
            if (parsed.isAfter(referenceDate)) return "Date of birth cannot be in the future"
        }
        return null
    }

    fun applyTo(account: PersonalAccount): PersonalAccount = account.copy(
        name = PersonName(firstName.trim(), lastName.trim(), middleName.trim().ifBlank { null }),
        gender = Gender(gender),
        dateOfBirth = dateOfBirth.trim().takeIf { it.isNotEmpty() }?.let(LocalDate::parse),
        address = if (listOf(addressLine1, city, region, postalCode, addressCountryCode).all { it.isBlank() }) null else PostalAddress(
            addressLine1 = addressLine1.trim(), city = city.trim(), region = region.trim().ifBlank { null },
            postalCode = postalCode.trim().ifBlank { null }, countryCode = addressCountryCode.trim().uppercase()
        ),
        citizenshipCountryCodes = citizenshipCountryCodes.map { it.trim().uppercase() }.filter { it.isNotBlank() }.toSet(),
        residence = if (residenceCountryCode.isBlank() && residencePlace.isBlank()) null else Residence(
            residenceCountryCode.trim().uppercase(), residencePlace.trim(), residencyStatus
        )
    )

    companion object {
        fun from(account: PersonalAccount) = ProfileDraft(
            firstName = account.name.firstName, lastName = account.name.lastName,
            middleName = account.name.middleName.orEmpty(), gender = account.gender.option,
            dateOfBirth = account.dateOfBirth?.toString().orEmpty(),
            addressLine1 = account.address?.addressLine1.orEmpty(), city = account.address?.city.orEmpty(),
            region = account.address?.region.orEmpty(), postalCode = account.address?.postalCode.orEmpty(),
            addressCountryCode = account.address?.countryCode.orEmpty(),
            citizenshipCountryCodes = account.citizenshipCountryCodes,
            residenceCountryCode = account.residence?.countryCode.orEmpty(),
            residencePlace = account.residence?.place.orEmpty(),
            residencyStatus = account.residence?.status ?: ResidencyStatus.Unknown
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountProfileScreen(
    account: PersonalAccount,
    onBack: () -> Unit,
    onSave: (PersonalAccount) -> Unit,
    onManageFamily: () -> Unit = {},
    referenceDate: LocalDate = LocalDate.now()
) {
    BackHandler(onBack = onBack)
    var draft by remember(account) { mutableStateOf(ProfileDraft.from(account)) }
    val citizenships = remember(account) { mutableStateListOf<String>().apply { addAll(account.citizenshipCountryCodes) } }
    var citizenshipInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Personal account") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).testTag("account_profile"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedButton(onClick = onManageFamily, modifier = Modifier.fillMaxWidth()) {
                    Text("Family members (${account.familyMembers.size})")
                }
            }
            item { Text("Personal details", style = MaterialTheme.typography.titleLarge) }
            item { Field("First name", draft.firstName) { draft = draft.copy(firstName = it) } }
            item { Field("Last name", draft.lastName) { draft = draft.copy(lastName = it) } }
            item { Field("Middle name", draft.middleName) { draft = draft.copy(middleName = it) } }
            item { EnumField("Gender", draft.gender, GenderOption.entries) { draft = draft.copy(gender = it) } }
            item { Field("Date of birth (YYYY-MM-DD)", draft.dateOfBirth) { draft = draft.copy(dateOfBirth = it) } }
            item {
                val age = runCatching { draft.dateOfBirth.takeIf(String::isNotBlank)?.let(LocalDate::parse)?.let { java.time.Period.between(it, referenceDate).years } }.getOrNull()
                if (age != null && age >= 0) Text("Age: $age")
            }
            item { Text("Address", style = MaterialTheme.typography.titleMedium) }
            item { Field("Address", draft.addressLine1) { draft = draft.copy(addressLine1 = it) } }
            item { Field("City", draft.city) { draft = draft.copy(city = it) } }
            item { Field("Region", draft.region) { draft = draft.copy(region = it) } }
            item { Field("Postal code", draft.postalCode) { draft = draft.copy(postalCode = it) } }
            item { Field("Country code", draft.addressCountryCode) { draft = draft.copy(addressCountryCode = it) } }
            item { Text("Citizenships", style = MaterialTheme.typography.titleMedium) }
            items(citizenships) { code ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(code)
                    TextButton(onClick = { citizenships.remove(code) }) { Text("Remove") }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(citizenshipInput, { citizenshipInput = it }, label = { Text("Country code") }, modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = {
                        citizenshipInput.trim().uppercase().takeIf { it.isNotBlank() && it !in citizenships }?.let(citizenships::add)
                        citizenshipInput = ""
                    }) { Text("Add") }
                }
            }
            item { Text("Current residence", style = MaterialTheme.typography.titleMedium) }
            item { Field("Residence country code", draft.residenceCountryCode) { draft = draft.copy(residenceCountryCode = it) } }
            item { Field("Place of residence", draft.residencePlace) { draft = draft.copy(residencePlace = it) } }
            item { EnumField("Residency status", draft.residencyStatus, ResidencyStatus.entries) { draft = draft.copy(residencyStatus = it) } }
            item { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item {
                Button(
                    onClick = {
                        val current = draft.copy(citizenshipCountryCodes = citizenships.toSet())
                        error = current.validationError(referenceDate)
                        if (error == null) onSave(current.applyTo(account))
                    },
                    modifier = Modifier.fillMaxWidth().testTag("account_save")
                ) { Text("Save profile") }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value, onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Enum<T>> EnumField(label: String, value: T, choices: List<T>, onValueChange: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }) {
        OutlinedTextField(
            value = value.name, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            choices.forEach { choice ->
                DropdownMenuItem(text = { Text(choice.name) }, onClick = { onValueChange(choice); expanded = false })
            }
        }
    }
}
