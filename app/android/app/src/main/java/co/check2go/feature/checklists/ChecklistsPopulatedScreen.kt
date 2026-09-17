package co.check2go.feature.checklists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppNavigationBar
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma CHECKLISTS_POPULATED state 1:28596. */
@Composable
fun ChecklistsPopulatedScreen(
    checklists: List<CompletedChecklist>,
    onCreateChecklist: () -> Unit,
    onQuickAdd: () -> Unit,
    onChecklistSelected: (Long) -> Unit,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAddLabel = stringResource(R.string.home_quick_add)
    Scaffold(
        modifier = modifier.fillMaxSize(), containerColor = Color(0xFFF4F7FA),
        topBar = { ChecklistsHeader() },
        bottomBar = { AppNavigationBar(AppDestination.Checklists, onDestinationSelected) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQuickAdd, shape = CircleShape,
                containerColor = Color(0xFF043CB3), contentColor = Color.White,
                modifier = Modifier.size(56.dp).semantics { contentDescription = quickAddLabel; role = Role.Button }
            ) { Text("+", fontSize = 29.sp, fontWeight = FontWeight.Light) }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            checklists.forEachIndexed { index, checklist ->
                ChecklistCard(checklist, index, { onChecklistSelected(checklist.id) })
            }
            CreateChecklistButton(onCreateChecklist, Modifier.padding(top = 8.dp))
            TeamChecklistsPromo(Modifier.padding(top = 32.dp, bottom = 16.dp))
        }
    }
}

@Composable
private fun ChecklistCard(checklist: CompletedChecklist, index: Int, onClick: () -> Unit) {
    val percent = checklist.completionPercent()
    val progressColor = when {
        percent == 100 -> Color(0xFFCCE8E4)
        index % 2 == 0 -> Color(0xFFE7D7E5)
        else -> Color(0xFFCAD8EF)
    }
    Box(
        Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(12.dp))
            .background(Color.White).clickable(onClickLabel = checklist.name, onClick = onClick)
            .testTag("checklist_card_${checklist.id}")
    ) {
        if (percent > 0) Box(
            Modifier.fillMaxHeight().fillMaxWidth(percent / 100f).background(progressColor)
        )
        Column(Modifier.align(Alignment.CenterStart).padding(horizontal = 10.dp)) {
            Text(checklist.name, color = Color(0xFF002349), fontSize = 15.sp, lineHeight = 18.sp)
            Text(stringResource(R.string.checklists_personal_context), color = Color(0xFF8D919A), fontSize = 12.sp)
        }
        Text(
            stringResource(R.string.checklists_completion_format, percent),
            Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
            color = Color(0xFF043CB3), fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChecklistsPopulatedPreview() = Check2GoTheme(false) {
    ChecklistsPopulatedScreen(
        listOf(
            CompletedChecklist(1L, "Before leaving"),
            CompletedChecklist(2L, "Documents")
        ), {}, {}, {}, {}
    )
}
