package co.check2go.feature.documents

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import co.check2go.R

data class DocumentDraft(
    val id: String? = null,
    val category: DocumentCategory = DocumentCategory.Identity,
    val typeId: String = DocumentCategory.Identity.types.first().id,
    val typeName: String = DocumentCategory.Identity.types.first().name,
    val custom: Boolean = false,
    val customTypeName: String = "",
    val name: String = "",
    val description: String = "",
    val countryOfIssue: String = "",
    val documentNumber: String = "",
    val issueDate: String = "",
    val expirationDate: String = "",
    val validFrom: String = "",
    val validUntil: String = "",
    val issuingAuthority: String = "",
    val citizenship: String = "",
    val destinationCountry: String = "",
    val notes: String = "",
    val attachments: List<DocumentAttachment> = emptyList(),
    val isPrimary: Boolean = false,
    val recommendations: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val version: Int = 1
) {
    val config: DocumentTypeConfig
        get() = if (custom) DocumentTypeConfig("custom", customTypeName.ifBlank { "Custom Document" }, category, DocumentField.entries.toSet(), category == DocumentCategory.Identity)
        else DocumentCatalog.type(category, typeId) ?: category.types.first()

    fun validationError(): String? {
        if (name.isBlank()) return "Document name is required"
        if (custom && customTypeName.isBlank()) return "Custom document type is required"
        listOf(issueDate, expirationDate, validFrom, validUntil).forEach { value ->
            if (value.isNotBlank()) try { LocalDate.parse(value.trim()) } catch (_: DateTimeParseException) { return "Use date format YYYY-MM-DD" }
        }
        val start = validFrom.toDateOrNull() ?: issueDate.toDateOrNull()
        val end = validUntil.toDateOrNull() ?: expirationDate.toDateOrNull()
        if (start != null && end != null && end.isBefore(start)) return "End date cannot be before start date"
        return null
    }

    fun toDocument(now: Instant = Instant.now()) = TravelDocument(
        id = id ?: UUID.randomUUID().toString(), category = category,
        typeId = if (custom && id == null) "custom-${UUID.randomUUID()}" else typeId,
        typeName = if (custom) "Custom Document" else typeName, customTypeName = customTypeName.trim().ifBlank { null },
        name = name.trim(), description = description.trim().ifBlank { null }, countryOfIssue = countryOfIssue.trim().ifBlank { null },
        documentNumber = documentNumber.trim().ifBlank { null }, issueDate = issueDate.toDateOrNull(), expirationDate = expirationDate.toDateOrNull(),
        validFrom = validFrom.toDateOrNull(), validUntil = validUntil.toDateOrNull(), issuingAuthority = issuingAuthority.trim().ifBlank { null },
        citizenship = citizenship.trim().ifBlank { null }, destinationCountry = destinationCountry.trim().ifBlank { null }, notes = notes.trim().ifBlank { null },
        attachments = attachments, isPrimary = isPrimary, useForTravelRecommendations = recommendations,
        source = if (attachments.isEmpty()) DocumentSource.Manual else DocumentSource.Imported,
        createdAt = createdAt, updatedAt = now, version = if (id == null) 1 else version + 1
    )

    companion object {
        fun create(category: DocumentCategory?, custom: Boolean): DocumentDraft {
            val selected = category ?: DocumentCategory.Identity
            val type = selected.types.first()
            return DocumentDraft(category = selected, typeId = type.id, typeName = type.name, custom = custom)
        }

        fun from(d: TravelDocument) = DocumentDraft(
            id = d.id, category = d.category, typeId = d.typeId, typeName = d.typeName, custom = d.customTypeName != null,
            customTypeName = d.customTypeName.orEmpty(), name = d.name, description = d.description.orEmpty(),
            countryOfIssue = d.countryOfIssue.orEmpty(), documentNumber = d.documentNumber.orEmpty(),
            issueDate = d.issueDate?.toString().orEmpty(), expirationDate = d.expirationDate?.toString().orEmpty(),
            validFrom = d.validFrom?.toString().orEmpty(), validUntil = d.validUntil?.toString().orEmpty(),
            issuingAuthority = d.issuingAuthority.orEmpty(), citizenship = d.citizenship.orEmpty(),
            destinationCountry = d.destinationCountry.orEmpty(), notes = d.notes.orEmpty(), attachments = d.attachments,
            isPrimary = d.isPrimary, recommendations = d.useForTravelRecommendations, createdAt = d.createdAt, version = d.version
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentEditorScreen(
    initial: DocumentDraft,
    hasDocumentConsent: Boolean,
    onAcceptDocumentConsent: () -> Unit,
    onImportAttachments: (List<Uri>, AttachmentRole) -> List<DocumentAttachment>,
    onOpenAttachment: (DocumentAttachment) -> Unit,
    onDeleteAttachment: (DocumentAttachment) -> Unit,
    onSave: (TravelDocument) -> Unit,
    onBack: () -> Unit
) {
    var draft by remember(initial) { mutableStateOf(initial) }
    var error by remember { mutableStateOf<String?>(null) }
    var consent by remember(hasDocumentConsent) { mutableStateOf(hasDocumentConsent) }
    var categoryMenu by remember { mutableStateOf(false) }
    var typeMenu by remember { mutableStateOf(false) }
    var importRole by remember { mutableStateOf(AttachmentRole.General) }
    var replacingId by remember { mutableStateOf<String?>(null) }
    var deletingAttachment by remember { mutableStateOf<DocumentAttachment?>(null) }
    var importing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val originalAttachmentIds = remember(initial) { initial.attachments.map { it.id }.toSet() }

    fun cancel() {
        if (importing) return
        draft.attachments.filterNot { it.id in originalAttachmentIds }.forEach(onDeleteAttachment)
        onBack()
    }
    BackHandler(enabled = !importing, onBack = ::cancel)

    fun import(uris: List<Uri>) {
        if (!consent || uris.isEmpty()) return
        val selectedRole = importRole
        val selectedReplacement = replacingId
        importing = true
        scope.launch {
            runCatching { withContext(Dispatchers.IO) { onImportAttachments(uris, selectedRole) } }
                .onSuccess { imported ->
                val retained = draft.attachments.filterNot { it.id == selectedReplacement }
                draft = draft.copy(attachments = retained + imported)
                replacingId = null
                }
                .onFailure { error = it.message ?: "Could not import attachment" }
            importing = false
        }
    }
    val multiplePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { import(it) }
    val singlePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { import(listOf(it)) } }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (draft.id == null) stringResource(R.string.document_editor_add) else stringResource(R.string.document_editor_edit)) },
            navigationIcon = { TextButton(onClick = ::cancel, enabled = !importing) { Text(stringResource(R.string.common_back)) } }
        )
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                Modifier.fillMaxWidth().widthIn(max = 720.dp).padding(horizontal = 16.dp).testTag("document_editor"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(stringResource(R.string.document_step_category), style = MaterialTheme.typography.titleMedium)
                    Box {
                        OutlinedButton(onClick = { categoryMenu = true }, modifier = Modifier.fillMaxWidth()) { Text(draft.category.localizedName()) }
                        DropdownMenu(categoryMenu, { categoryMenu = false }) {
                            DocumentCategory.entries.forEach { category -> DropdownMenuItem({ Text(category.localizedName()) }, {
                                val first = category.types.first()
                                draft = draft.copy(category = category, typeId = first.id, typeName = first.name)
                                categoryMenu = false
                            }) }
                        }
                    }
                }
                item {
                    Text(stringResource(R.string.document_step_type), style = MaterialTheme.typography.titleMedium)
                    if (draft.custom) {
                        EditorField(stringResource(R.string.document_custom_type), draft.customTypeName) { draft = draft.copy(customTypeName = it) }
                    } else {
                        Box {
                            OutlinedButton(onClick = { typeMenu = true }, modifier = Modifier.fillMaxWidth()) { Text(draft.config.localizedName()) }
                            DropdownMenu(typeMenu, { typeMenu = false }) {
                                draft.category.types.forEach { type -> DropdownMenuItem({ Text(type.localizedName()) }, {
                                    draft = draft.copy(typeId = type.id, typeName = type.name)
                                    typeMenu = false
                                }) }
                            }
                        }
                    }
                    TextButton(onClick = { draft = draft.copy(custom = !draft.custom, customTypeName = "") }) {
                        Text(if (draft.custom) stringResource(R.string.document_predefined_type) else "+ " + stringResource(R.string.documents_add_custom))
                    }
                }
                item { Text(stringResource(R.string.document_step_details), style = MaterialTheme.typography.titleMedium) }
                item { EditorField(stringResource(R.string.document_name), draft.name) { draft = draft.copy(name = it) } }
                if (draft.custom) item { EditorField(stringResource(R.string.document_description), draft.description, 3) { draft = draft.copy(description = it) } }
                val fields = draft.config.fields
                if (DocumentField.CountryOfIssue in fields) item { EditorField(stringResource(R.string.document_country_issue), draft.countryOfIssue) { draft = draft.copy(countryOfIssue = it) } }
                if (DocumentField.DocumentNumber in fields) item { EditorField(stringResource(R.string.document_number), draft.documentNumber) { draft = draft.copy(documentNumber = it) } }
                if (DocumentField.IssueDate in fields) item { EditorField(stringResource(R.string.document_issue_date), draft.issueDate) { draft = draft.copy(issueDate = it) } }
                if (DocumentField.ExpirationDate in fields) item { EditorField(stringResource(R.string.document_expiration_date), draft.expirationDate) { draft = draft.copy(expirationDate = it) } }
                if (DocumentField.ValidFrom in fields) item { EditorField(stringResource(R.string.document_valid_from), draft.validFrom) { draft = draft.copy(validFrom = it) } }
                if (DocumentField.ValidUntil in fields) item { EditorField(stringResource(R.string.document_valid_until), draft.validUntil) { draft = draft.copy(validUntil = it) } }
                if (DocumentField.IssuingAuthority in fields) item { EditorField(stringResource(R.string.document_issuing_authority), draft.issuingAuthority) { draft = draft.copy(issuingAuthority = it) } }
                if (DocumentField.Citizenship in fields) item { EditorField(stringResource(R.string.document_citizenship), draft.citizenship) { draft = draft.copy(citizenship = it) } }
                if (DocumentField.DestinationCountry in fields) item { EditorField(stringResource(R.string.document_destination), draft.destinationCountry) { draft = draft.copy(destinationCountry = it) } }
                if (DocumentField.Notes in fields) item { EditorField(stringResource(R.string.document_notes), draft.notes, 4) { draft = draft.copy(notes = it) } }
                if (draft.config.supportsPrimary) {
                    item { CheckRow(stringResource(R.string.document_primary), draft.isPrimary) { draft = draft.copy(isPrimary = it) } }
                    item { CheckRow(stringResource(R.string.document_recommendations), draft.recommendations) { draft = draft.copy(recommendations = it) } }
                }
                item { Text(stringResource(R.string.document_step_attachments), style = MaterialTheme.typography.titleMedium) }
                if (!hasDocumentConsent) {
                    item {
                        Column {
                            Text(stringResource(R.string.document_sensitive_notice))
                            CheckRow(stringResource(R.string.document_upload_consent), consent) {
                                consent = it
                                if (it) onAcceptDocumentConsent()
                            }
                        }
                    }
                }
                draft.attachments.forEach { attachment ->
                    item(key = attachment.id) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(attachment.displayName, maxLines = 1)
                                Text("${attachment.mimeType} · ${attachment.role.name}", style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { onOpenAttachment(attachment) }) { Text(stringResource(R.string.common_view)) }
                            TextButton(onClick = {
                                importRole = attachment.role; replacingId = attachment.id
                                singlePicker.launch(LocalDocumentStore.ALLOWED_MIME_TYPES.toTypedArray())
                            }, enabled = consent && !importing) { Text(stringResource(R.string.document_replace)) }
                            TextButton(onClick = { deletingAttachment = attachment }) {
                                Text(stringResource(R.string.common_delete), color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            importRole = AttachmentRole.General; replacingId = null
                            multiplePicker.launch(LocalDocumentStore.ALLOWED_MIME_TYPES.toTypedArray())
                        }, enabled = consent && !importing, modifier = Modifier.weight(1f)) { Text(if (importing) stringResource(R.string.document_uploading) else stringResource(R.string.document_upload_files)) }
                        if (draft.category == DocumentCategory.Identity) {
                            OutlinedButton(onClick = {
                                importRole = AttachmentRole.FrontSide; replacingId = null
                                singlePicker.launch(arrayOf("image/jpeg", "image/png"))
                            }, enabled = consent && !importing, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.document_front_side)) }
                        }
                    }
                }
                if (draft.category == DocumentCategory.Identity) {
                    item {
                        OutlinedButton(onClick = {
                            importRole = AttachmentRole.BackSide; replacingId = null
                            singlePicker.launch(arrayOf("image/jpeg", "image/png"))
                        }, enabled = consent && !importing, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.document_back_side)) }
                    }
                }
                item { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                item {
                    Button(onClick = {
                        error = draft.validationError()
                        if (error == null) onSave(draft.toDocument())
                }, enabled = !importing, modifier = Modifier.fillMaxWidth().testTag("document_save")) { Text(stringResource(R.string.document_save)) }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    deletingAttachment?.let { attachment ->
        AlertDialog(
            onDismissRequest = { deletingAttachment = null },
            title = { Text("Delete attachment?") },
            text = { Text("${attachment.displayName} will be removed from this document.") },
            confirmButton = {
                TextButton(onClick = {
                    if (attachment.id !in originalAttachmentIds) onDeleteAttachment(attachment)
                    draft = draft.copy(attachments = draft.attachments - attachment)
                    deletingAttachment = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deletingAttachment = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EditorField(label: String, value: String, lines: Int = 1, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), singleLine = lines == 1, minLines = lines)
}

@Composable
private fun CheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, onCheckedChange)
        Text(label, Modifier.weight(1f))
    }
}

private fun String.toDateOrNull(): LocalDate? = trim().takeIf(String::isNotEmpty)?.let(LocalDate::parse)
