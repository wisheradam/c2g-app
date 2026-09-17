package co.check2go.feature.events

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

/** Approved Figma Events Calendar settings sheet 1:28630. */
@Composable
fun EventCalendarScreen(onBack: () -> Unit, onSave: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Color(0x99002349))) {
        Box(Modifier.fillMaxSize().clickable(onClick = onBack).testTag("events_calendar_scrim"))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(548.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Color.White)
                .navigationBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select events dates", color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp)); EventCalendarCard()
            Text("Fri, 9 June", Modifier.padding(top = 24.dp), color = Color(0xFF043CB3), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Box(
                Modifier.padding(top = 24.dp).fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF043CB3)).clickable { onSave("2024-06-09") }.testTag("events_save_date"),
                contentAlignment = Alignment.Center
            ) { Text("Save date", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun EventCalendarCard() {
    Column(Modifier.fillMaxWidth().height(314.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFCFCFD))) {
        Row(Modifier.fillMaxWidth().height(34.dp).background(Color(0xFFF8F8F9)), verticalAlignment = Alignment.CenterVertically) {
            listOf("SUN","MON","TUE","WED","THU","FRI","SAT").forEach { Text(it, Modifier.weight(1f), color = Color(0xFFC3C4C7), fontSize = 13.sp, textAlign = TextAlign.Center) }
        }
        Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("June 2024", color = Color(0xFF002349), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("  ›", color = Color(0xFF043CB3), fontSize = 24.sp); Spacer(Modifier.weight(1f)); Text("‹    ›", color = Color(0xFF043CB3), fontSize = 24.sp)
        }
        val weeks = listOf(listOf<Int?>(null,null,null,null,null,null,1), listOf(2,3,4,5,6,7,8), listOf(9,10,11,12,13,14,15), listOf(16,17,18,19,20,21,22), listOf(23,24,25,26,27,28,29), listOf(30,null,null,null,null,null,null))
        weeks.forEach { week -> Row(Modifier.fillMaxWidth().weight(1f)) { week.forEach { day -> Box(Modifier.weight(1f).fillMaxHeight().background(if (day == 9) Color(0xFF043CB3) else Color.Transparent), contentAlignment = Alignment.Center) { if (day != null) Text(day.toString(), color = if (day == 9) Color.White else Color(0xFF002349), fontSize = 16.sp) } } } }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun EventCalendarPreview() = Check2GoTheme(false) { EventCalendarScreen({}, {}) }
