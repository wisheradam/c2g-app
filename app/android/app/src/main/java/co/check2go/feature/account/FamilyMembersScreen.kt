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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import java.time.format.DateTimeParseException

data class FamilyMemberDraft(
    val id: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val gender: GenderOption = GenderOption.Unspecified,
    val citizenships: String = "",
    val residenceCountryCode: String = "",
    val relationship: RelationshipType = RelationshipType.Other,
    val customRelationship: String = ""
) {
    fun validationError(referenceDate: LocalDate): String? {
        if (firstName.isBlank() || lastName.isBlank()) return "First and last name are required"
        if (relationship == RelationshipType.Other && customRelationship.isBlank()) return "Describe the relationship"
        if (dateOfBirth.isNotBlank()) {
            val date = try { LocalDate.parse(dateOfBirth.trim()) } catch (_: DateTimeParseException) {
                return "Use date format YYYY-MM-DD"
            }
            if (date.isAfter(referenceDate)) return "Date of birth cannot be in the future"
        }
        return null
    }

    fun toMember(newId: () -> String): FamilyMember = FamilyMember(
        id = id ?: newId(),
        name = PersonName(firstName.trim(), lastName.trim()),
        dateOfBirth = dateOfBirth.trim().takeIf(String::isNotEmpty)?.let(LocalDate::parse),
        gender = Gender(gender),
        citizenshipCountryCodes = citizenships.split(',').map(String::trim).filter(String::isNotEmpty).map(String::uppercase).toSet(),
        residenceCountryCode = residenceCountryCode.trim().uppercase().ifBlank { null },
        relationship = Relationship(relationship, customRelationship.trim().takeIf { relationship == RelationshipType.Other })
    )

    companion object {
        fun from(member: FamilyMember) = FamilyMemberDraft(
            id = member.id, firstName = member.name.firstName, lastName = member.name.lastName,
            dateOfBirth = member.dateOfBirth?.toString().orEmpty(), gender = member.gender.option,
            citizenships = member.citizenshipCountryCodes.joinToString(", "),
            residenceCountryCode = member.residenceCountryCode.orEmpty(),
            relationship = member.relationship.type,
            customRelationship = member.relationship.customLabel.orEmpty()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMembersScreen(
    account: PersonalAccount,
    onAccountChange: (PersonalAccount) -> Unit,
    onBack: () -> Unit,
    referenceDate: LocalDate = LocalDate.now()
) {
    BackHandler(onBack = onBack)
    var editor by remember { mutableStateOf<FamilyMemberDraft?>(null) }

    if (editor != null) {
        FamilyMemberEditor(
            initial = editor!!,
            referenceDate = referenceDate,
            onCancel = { editor = null },
            onSave = { draft ->
                val member = draft.toMember { "family-${System.nanoTime()}" }
                val members = account.familyMembers.filterNot { it.id == member.id } + member
                onAccountChange(account.copy(familyMembers = members))
                editor = null
            }
        )
        return
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Family members") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } })
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Button(onClick = { editor = FamilyMemberDraft() }, modifier = Modifier.fillMaxWidth()) { Text("Add family member") } }
            if (account.familyMembers.isEmpty()) item { Text("No family members added") }
            items(account.familyMembers, key = { it.id }) { member ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${member.name.firstName} ${member.name.lastName}", style = MaterialTheme.typography.titleMedium)
                        Text(member.relationship.customLabel ?: member.relationship.type.name)
                        member.ageOn(referenceDate)?.let { Text("Age: $it") }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { editor = FamilyMemberDraft.from(member) }) { Text("Edit") }
                            TextButton(onClick = { onAccountChange(account.withFamilyMemberRemoved(member.id)) }) { Text("Remove") }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FamilyMemberEditor(
    initial: FamilyMemberDraft,
    referenceDate: LocalDate,
    onCancel: () -> Unit,
    onSave: (FamilyMemberDraft) -> Unit
) {
    var draft by remember(initial) { mutableStateOf(initial) }
    var error by remember { mutableStateOf<String?>(null) }
    Scaffold(topBar = {
        TopAppBar(title = { Text(if (draft.id == null) "Add family member" else "Edit family member") }, navigationIcon = { TextButton(onClick = onCancel) { Text("Cancel") } })
    }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { FamilyField("First name", draft.firstName) { draft = draft.copy(firstName = it) } }
            item { FamilyField("Last name", draft.lastName) { draft = draft.copy(lastName = it) } }
            item { FamilyField("Date of birth (YYYY-MM-DD)", draft.dateOfBirth) { draft = draft.copy(dateOfBirth = it) } }
            item { FamilyEnumField("Gender", draft.gender, GenderOption.entries) { draft = draft.copy(gender = it) } }
            item { FamilyField("Citizenships (comma separated)", draft.citizenships) { draft = draft.copy(citizenships = it) } }
            item { FamilyField("Country of residence", draft.residenceCountryCode) { draft = draft.copy(residenceCountryCode = it) } }
            item { FamilyEnumField("Relationship", draft.relationship, RelationshipType.entries) { draft = draft.copy(relationship = it) } }
            if (draft.relationship == RelationshipType.Other) {
                item { FamilyField("Relationship description", draft.customRelationship) { draft = draft.copy(customRelationship = it) } }
            }
            item { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            item {
                Button(onClick = {
                    error = draft.validationError(referenceDate)
                    if (error == null) onSave(draft)
                }, modifier = Modifier.fillMaxWidth()) { Text("Save family member") }
            }
        }
    }
}

@Composable
private fun FamilyField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Enum<T>> FamilyEnumField(label: String, value: T, values: List<T>, onChange: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }) {
        OutlinedTextField(value.name, {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
        ExposedDropdownMenu(expanded, { expanded = false }) {
            values.forEach { option -> DropdownMenuItem({ Text(option.name) }, { onChange(option); expanded = false }) }
        }
    }
}
