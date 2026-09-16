package co.check2go.feature.checklists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma Share checklist subscription sheet 1:28023. */
@Composable
fun ShareChecklistScreen(onBack: () -> Unit, onFindOutMore: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Color(0x99002349))) {
        Box(Modifier.fillMaxSize().clickable(onClick = onBack).testTag("share_scrim"))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Color.White)
                .navigationBarsPadding().padding(horizontal = 16.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Share checklist", color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "You can easily share checklists with other\nusers. To do this you need to purchase a\nsubscription of $5 per month.",
                Modifier.padding(top = 8.dp), color = Color(0xFF002349), fontSize = 16.sp,
                lineHeight = 24.sp, textAlign = TextAlign.Center
            )
            Text(
                "You also have access to a 7 days free trial period",
                Modifier.padding(top = 22.dp), color = Color(0xFF002349), fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
            )
            Box(
                Modifier.padding(top = 24.dp).fillMaxWidth().height(50.dp).clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFF043CB3)).clickable(onClick = onFindOutMore).testTag("share_find_out_more"),
                contentAlignment = Alignment.Center
            ) { Text("Find out more", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun ShareChecklistPreview() = Check2GoTheme(false) { ShareChecklistScreen({}, {}) }
