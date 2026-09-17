package co.check2go.feature.checklists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma active-checklist states 1:27947, 1:27985 and 1:29908. */
@Composable
fun ChecklistDetailScreen(
    checklist: CompletedChecklist,
    onToggleItem: (Long) -> Unit,
    onEditChecklist: () -> Unit,
    onDuplicateChecklist: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onShareChecklist: () -> Unit = {}
) {
    val percent = checklist.completionPercent()
    Scaffold(
        modifier = modifier.fillMaxSize(), containerColor = Color(0xFFF4F7FA),
        topBar = { ChecklistDetailHeader(checklist.name, onBack) },
        bottomBar = { ChecklistDetailBottom(percent, onShareChecklist) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            if (checklist.items.isEmpty()) {
                Text(stringResource(R.string.checklist_detail_no_items), color = Color(0xFF8D919A))
            }
            checklist.sections.forEach { section ->
                val items = checklist.items.filter { it.sectionId == section.id }
                if (items.isNotEmpty()) ChecklistItemGroup(section.name, items, onToggleItem)
            }
            val ids = checklist.sections.map { it.id }.toSet()
            val unsectioned = checklist.items.filter { it.sectionId == null || it.sectionId !in ids }
            if (unsectioned.isNotEmpty()) ChecklistItemGroup(stringResource(R.string.checklist_no_section), unsectioned, onToggleItem)

            if (checklist.name.equals("Transfer", ignoreCase = true)) {
                TransferPromotion()
            } else {
                TeamChecklistPromotion()
            }

            Spacer(Modifier.height(22.dp))
            NotificationCard()
            NotificationCard()
            Box(
                Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFE3EAF3)),
                contentAlignment = Alignment.Center
            ) { Text("＋  Add notification", color = Color(0xFF043CB3), fontSize = 16.sp) }
            Spacer(Modifier.height(24.dp))
            DetailAction("✎", stringResource(R.string.checklist_edit_action), onEditChecklist)
            DetailAction("▣", stringResource(R.string.checklist_duplicate_action), onDuplicateChecklist)
            DetailAction("♙", "Delete checklist", {}, Color(0xFFC23C68))
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NotificationCard() {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp).testTag("checklist_notification")
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text("Notification", Modifier.weight(1f), color = Color(0xFF002349), fontSize = 16.sp)
            Text("×", color = Color(0xFF8D919A), fontSize = 20.sp)
        }
        Text("Jun 9, 2024 at 9:41 AM", color = Color(0xFF043CB3), fontSize = 14.sp)
    }
}

/** Approved people-free team promotion used by checklist states 1:29908 and 1:29981. */
@Composable
private fun TeamChecklistPromotion() {
    Box(
        Modifier.fillMaxWidth().height(183.dp).clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF4F8FC4), Color(0xFF84E7FF))))
            .padding(16.dp).testTag("team_checklists_promo")
    ) {
        Column(Modifier.fillMaxSize()) {
            Text("Team checklists", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text(
                "You can easily share\nchecklists with other users.\nTry 7 days free trial period",
                color = Color.White, fontSize = 15.sp, lineHeight = 20.sp
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
                contentAlignment = Alignment.Center
            ) { Text("Find out more", color = Color(0xFF002349), fontSize = 16.sp) }
        }
        Text("●  ◆  ●", Modifier.align(Alignment.CenterEnd), color = Color(0xFF7047E8), fontSize = 25.sp)
        Text("×", Modifier.align(Alignment.TopEnd), color = Color.White, fontSize = 20.sp)
    }
}

/** Approved people-free car-rental promotion used by Transfer state 1:29943. */
@Composable
private fun TransferPromotion() {
    Box(
        Modifier.fillMaxWidth().height(242.dp).clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(Color.White, Color(0xFFFFF4D9))))
            .padding(16.dp).testTag("transfer_promo")
    ) {
        Column(Modifier.fillMaxSize()) {
            Text("Move on your own\nto all the sights", color = Color(0xFF002349), fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.size(76.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFFF4B00)), contentAlignment = Alignment.Center) {
                    Text("SIXT", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }
                Box(Modifier.size(76.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFE8003F)), contentAlignment = Alignment.Center) {
                    Text("AVIS", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }
                Text("🚕", fontSize = 46.sp)
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFE9EEF5)),
                contentAlignment = Alignment.Center
            ) { Text("Request for car rental", color = Color(0xFF043CB3), fontSize = 16.sp) }
        }
        Text("×", Modifier.align(Alignment.TopEnd), color = Color(0xFF8D919A), fontSize = 20.sp)
    }
}

@Composable
private fun ChecklistDetailHeader(title: String, onBack: () -> Unit) {
    val muted = Color(0xFF8D919A)
    val back = stringResource(R.string.trip_back)
    Row(
        Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.c2g_arrow_left), null,
            Modifier.size(20.dp).clickable(onClick = onBack).semantics { text = AnnotatedString(back) },
            colorFilter = ColorFilter.tint(muted)
        )
        Spacer(Modifier.weight(1f)); Text(title, color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f))
        listOf(R.drawable.c2g_settings, R.drawable.c2g_help, R.drawable.c2g_notification).forEachIndexed { index, icon ->
            if (index > 0) Spacer(Modifier.width(12.dp))
            Image(painterResource(icon), null, Modifier.size(21.dp), colorFilter = ColorFilter.tint(muted))
        }
    }
}

@Composable
private fun ChecklistItemGroup(title: String, items: List<SavedChecklistItem>, onToggleItem: (Long) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Color(0xFF002349), fontSize = 13.sp, modifier = Modifier.padding(start = 12.dp))
        items.forEach { item -> ChecklistItemRow(item) { onToggleItem(item.id) } }
    }
}

@Composable
private fun ChecklistItemRow(item: SavedChecklistItem, onToggle: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
            .testTag("checklist_detail_item_row_${item.id}")
            .toggleable(item.completed, onValueChange = { onToggle() }, role = Role.Checkbox)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                item.completed, null, Modifier.size(20.dp),
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2FC768), uncheckedColor = Color(0xFFB5BAC2), checkmarkColor = Color.White)
            )
            Spacer(Modifier.width(7.dp))
            Text(item.name, Modifier.weight(1f), color = Color(0xFF002349), fontSize = 15.sp)
            if (item.includeFile) Box(
                Modifier.clip(RoundedCornerShape(18.dp)).background(Color(0xFFF0F3F7)).padding(horizontal = 12.dp, vertical = 8.dp)
            ) { Text("+ Upload file", color = Color(0xFF043CB3), fontSize = 14.sp) }
        }
        if (item.includeFile) Text(
            stringResource(R.string.checklist_detail_upload_file_unavailable),
            color = Color(0xFF8D919A), fontSize = 11.sp,
            modifier = Modifier.padding(start = 27.dp, top = 2.dp).testTag("checklist_detail_upload_file_note_${item.id}")
        )
    }
}

@Composable
private fun DetailAction(icon: String, label: String, onClick: () -> Unit, color: Color = Color(0xFF002349)) {
    Row(
        Modifier.fillMaxWidth().height(32.dp).clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically
    ) { Text(icon, color = color, fontSize = 21.sp, modifier = Modifier.width(32.dp)); Text(label, color = color, fontSize = 17.sp) }
}

@Composable
private fun ChecklistDetailBottom(percent: Int, onShareChecklist: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFE1E4E8))) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(percent / 100f).background(if (percent == 100) Color(0xFF2FC768) else Color(0xFFC23C68)))
        }
        Text(stringResource(R.string.checklists_completion_format, percent), Modifier.testTag("checklist_detail_completion"), color = Color(0xFF8D919A), fontSize = 14.sp)
        Box(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp).fillMaxWidth().height(50.dp)
                .clip(RoundedCornerShape(10.dp)).background(Color(0xFF043CB3))
                .clickable(onClick = onShareChecklist).testTag("share_checklist_action"), contentAlignment = Alignment.Center
        ) { Text("↗  Share checklist", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChecklistDetailPreview() = Check2GoTheme(false) {
    ChecklistDetailScreen(
        CompletedChecklist(1, "Documents", items = listOf(SavedChecklistItem(1, "Passport", null, true, true))),
        {}, {}, {}, {}
    )
}
