package co.check2go.feature.documents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppHeader
import co.check2go.core.design.AppNavigationBar
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun DocumentsScreen(
    documents: List<TravelDocument>,
    onAddDocument: (DocumentCategory?, Boolean) -> Unit,
    onEditDocument: (TravelDocument) -> Unit,
    onDeleteDocument: (TravelDocument) -> Unit,
    onOpenAttachment: (DocumentAttachment) -> Unit,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(DocumentSort.Expiration) }
    var sortExpanded by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<TravelDocument?>(null) }
    val expanded = remember { mutableStateMapOf<DocumentCategory, Boolean>() }
    BackHandler { onDestinationSelected(AppDestination.Home) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppHeader() },
        bottomBar = { AppNavigationBar(AppDestination.Documents, onDestinationSelected) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().widthIn(max = 840.dp).padding(horizontal = 16.dp).testTag("documents_page"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Documents", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                        Text("Store and manage your travel documents in one place.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                item {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().testTag("documents_search"),
                        label = { Text("Search documents...") }, singleLine = true
                    )
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onAddDocument(null, false) }, modifier = Modifier.weight(1f).testTag("documents_add")) { Text("+ Add Document") }
                        Box {
                            OutlinedButton(onClick = { sortExpanded = true }) { Text("Sort: ${sort.label}") }
                            DropdownMenu(sortExpanded, { sortExpanded = false }) {
                                DocumentSort.entries.forEach { option ->
                                    DropdownMenuItem({ Text(option.label) }, { sort = option; sortExpanded = false })
                                }
                            }
                        }
                    }
                }

                DocumentCategory.entries.forEach { category ->
                    val matches = documents.filter { it.category == category && it.matches(query) }.sortedByDocument(sort)
                    val categoryExpanded = if (query.isNotBlank()) matches.isNotEmpty() else expanded[category] == true
                    item(key = "header-${category.name}") {
                        CategoryHeader(category, documents.count { it.category == category }, categoryExpanded) {
                            expanded[category] = !(expanded[category] ?: false)
                        }
                    }
                    if (categoryExpanded) {
                        if (matches.isEmpty()) {
                            item(key = "empty-${category.name}") {
                                Text("No ${category.displayName.lowercase()} documents added yet.", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            items(matches, key = { it.id }) { document ->
                                DocumentRow(document, onEditDocument, { deleting = document }, onOpenAttachment)
                            }
                        }
                        item(key = "actions-${category.name}") {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { onAddDocument(category, false) }, modifier = Modifier.weight(1f)) { Text("+ Add Document") }
                                TextButton(onClick = { onAddDocument(category, true) }, modifier = Modifier.weight(1f)) { Text("+ Add Custom Document") }
                            }
                        }
                    }
                }
                if (query.isNotBlank() && documents.none { it.matches(query) }) {
                    item { Text("No documents match “$query”.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    deleting?.let { document ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete document?") },
            text = { Text("${document.name} and its attachments will be permanently removed from this device.") },
            confirmButton = { TextButton(onClick = { onDeleteDocument(document); deleting = null }, modifier = Modifier.testTag("document_delete_confirm")) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CategoryHeader(category: DocumentCategory, count: Int, expanded: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick).testTag("category_${category.name}")) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(34.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = .1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(category.icon, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
            }
            Text(category.displayName, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("$count", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(if (expanded) "▲" else "▼", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DocumentRow(
    document: TravelDocument,
    onEdit: (TravelDocument) -> Unit,
    onDelete: () -> Unit,
    onOpenAttachment: (DocumentAttachment) -> Unit
) {
    val status = document.status()
    Card(Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("document_${document.id}")) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(document.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(document.customTypeName ?: document.typeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusPill(status)
            }
            document.countryOfIssue?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            val end = document.validUntil ?: document.expirationDate
            if (end != null) {
                val days = ChronoUnit.DAYS.between(LocalDate.now(), end)
                Text(if (days >= 0) "Expires: $end · $days days" else "Expired: $end", style = MaterialTheme.typography.bodySmall)
            }
            if (document.isPrimary) Text("✓ Primary document", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            if (document.useForTravelRecommendations) Text("✓ Use for travel recommendations", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                document.attachments.firstOrNull()?.let { attachment -> TextButton(onClick = { onOpenAttachment(attachment) }) { Text("View") } }
                TextButton(onClick = { onEdit(document) }, modifier = Modifier.testTag("document_edit_${document.id}")) { Text(if (document.attachments.isEmpty()) "Edit" else "Edit / Replace file") }
                TextButton(onClick = onDelete, modifier = Modifier.testTag("document_delete_${document.id}")) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun StatusPill(status: DocumentStatus) {
    val color = when (status) {
        DocumentStatus.Valid -> Color(0xFF2E7D32)
        DocumentStatus.ExpiringSoon -> Color(0xFFB26A00)
        DocumentStatus.Expired -> MaterialTheme.colorScheme.error
        DocumentStatus.Future -> Color(0xFF1565C0)
        DocumentStatus.MissingExpiration -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(status.label, color = color, style = MaterialTheme.typography.labelSmall, modifier = Modifier.background(color.copy(alpha = .1f), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
}
