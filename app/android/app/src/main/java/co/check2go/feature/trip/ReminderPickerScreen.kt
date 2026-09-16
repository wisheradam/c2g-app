package co.check2go.feature.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma reminder/deadline picker states 1:29068–1:29160. */
@Composable
fun ReminderPickerScreen(
    date: String,
    onDateChange: (String) -> Unit,
    time: String,
    onTimeChange: (String) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val draft = ReminderDraft(date, time)
    val primary = Color(0xFF043CB3)
    val ink = Color(0xFF002349)
    val muted = Color(0xFF8D919A)

    Box(modifier.fillMaxSize().background(Color(0xFF414141))) {
        Box(
            Modifier.fillMaxWidth().fillMaxHeight().padding(top = 15.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
        ) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 39.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Select date and time for reminder",
                    color = ink, fontSize = 18.sp, lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(16.dp))
                CalendarCard(selectedDay = draft.parsedDate()?.dayOfMonth)
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PickerField(date, onDateChange, "Jun 20, 2023", stringResource(R.string.reminder_date_label), "reminder_date_field", 122, ImeAction.Next)
                    PickerField(time, onTimeChange, "9:41 AM", stringResource(R.string.reminder_time_label), "reminder_time_field", 84, ImeAction.Done)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(11.dp))
                        .background(if (draft.isValid()) primary else Color(0xFFE1E2E4))
                        .clickable(enabled = draft.isValid(), onClick = onConfirm)
                        .testTag("reminder_set_action"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.reminder_set_action),
                        color = if (draft.isValid()) Color.White else muted,
                        fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            // The approved sheet closes through its grabber/system back. Keep the established
            // accessible Back action transparent so behavior remains unchanged.
            Text(
                stringResource(R.string.trip_back),
                Modifier.align(Alignment.TopStart).size(48.dp).alpha(0f).clickable(onClick = onBack)
            )
        }
        Box(
            Modifier.align(Alignment.TopCenter).padding(top = 1.dp).width(48.dp).height(5.dp)
                .clip(RoundedCornerShape(3.dp)).background(Color.White)
        )
    }
}

@Composable
private fun CalendarCard(selectedDay: Int?) {
    val ink = Color(0xFF002349)
    val primary = Color(0xFF043CB3)
    Column(
        Modifier.fillMaxWidth().height(314.dp).clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFCFCFD))
    ) {
        Row(
            Modifier.fillMaxWidth().height(33.dp).background(Color(0xFFF8F8F9)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach {
                Text(it, Modifier.weight(1f), color = Color(0xFFC3C4C7), fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        }
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("June 2024", color = ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("›", Modifier.padding(start = 10.dp), color = primary, fontSize = 27.sp)
            Spacer(Modifier.weight(1f))
            Text("‹", color = primary, fontSize = 25.sp)
            Spacer(Modifier.width(24.dp))
            Text("›", color = primary, fontSize = 25.sp)
        }
        val weeks = listOf(
            listOf(null, null, null, null, null, null, 1),
            listOf(2, 3, 4, 5, 6, 7, 8),
            listOf(9, 10, 11, 12, 13, 14, 15),
            listOf(16, 17, 18, 19, 20, 21, 22),
            listOf(23, 24, 25, 26, 27, 28, 29),
            listOf(30, null, null, null, null, null, null)
        )
        weeks.forEach { week ->
            Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
                week.forEach { day ->
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        if (day != null) Box(
                            Modifier.size(44.dp).background(if (day == selectedDay) primary else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(day.toString(), color = if (day == selectedDay) Color.White else ink, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    tag: String,
    width: Int,
    imeAction: ImeAction
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.width(width.dp).height(36.dp).clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFECEDEF)).testTag(tag).semantics { text = AnnotatedString(label) },
        singleLine = true,
        textStyle = TextStyle(Color(0xFF043CB3), 16.sp, textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = imeAction),
        decorationBox = { inner ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (value.isEmpty()) Text(placeholder, color = Color(0xFF8D919A), fontSize = 16.sp)
                inner()
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReminderPickerPreview() = Check2GoTheme(false) {
    ReminderPickerScreen("2024-06-09", {}, "09:41", {}, {}, {})
}
