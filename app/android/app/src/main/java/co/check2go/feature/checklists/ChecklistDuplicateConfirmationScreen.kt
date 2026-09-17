package co.check2go.feature.checklists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma duplicate-success bottom sheet 1:28318. */
@Composable
fun ChecklistDuplicateConfirmationScreen(
    checklistName: String,
    onGoBack: () -> Unit,
    onUseNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize().background(Color(0x99002349))) {
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Color.White)
                .navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.width(48.dp).height(5.dp).clip(RoundedCornerShape(3.dp)).background(Color.White))
            Spacer(Modifier.height(35.dp))
            Box(Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF2FC768)), contentAlignment = Alignment.Center) {
                Text("▣", color = Color.White, fontSize = 21.sp)
            }
            Spacer(Modifier.height(17.dp))
            Text(
                stringResource(R.string.checklist_duplicate_confirmation_title),
                color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text("Now you can use it!", Modifier.padding(top = 4.dp), color = Color(0xFF002349), fontSize = 16.sp)
            Text(checklistName, Modifier.padding(top = 2.dp), color = Color(0xFF8D919A), fontSize = 12.sp)
            Spacer(Modifier.height(25.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetButton(
                    stringResource(R.string.checklist_duplicate_go_back), onGoBack,
                    Color(0xFFF0F3F7), Color(0xFF043CB3), "checklist_duplicate_go_back", Modifier.weight(1f)
                )
                SheetButton(
                    stringResource(R.string.checklist_duplicate_use_now), onUseNow,
                    Color(0xFF043CB3), Color.White, "checklist_duplicate_use_now", Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SheetButton(label: String, onClick: () -> Unit, background: Color, foreground: Color, tag: String, modifier: Modifier) {
    Box(
        modifier.height(41.dp).clip(RoundedCornerShape(10.dp)).background(background)
            .clickable(onClick = onClick).testTag(tag), contentAlignment = Alignment.Center
    ) { Text(label, color = foreground, fontSize = 16.sp) }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChecklistDuplicateConfirmationPreview() = Check2GoTheme(false) {
    ChecklistDuplicateConfirmationScreen("Before leaving", {}, {})
}
