package co.check2go.feature.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

data class TripDatesDraft(
    val oneWay: Boolean,
    val departureDate: String,
    val returnDate: String,
    val reminderEnabled: Boolean,
    val reminderDate: String = "",
    val reminderTime: String = ""
)

/** Approved Figma travel-dates states 1:28970, 1:28694, 1:28835, 1:28880 and 1:28925. */
@Composable
fun TripCreateDatesScreen(
    oneWay: Boolean,
    onOneWayChange: (Boolean) -> Unit,
    departureDate: String,
    onDepartureDateChange: (String) -> Unit,
    returnDate: String,
    onReturnDateChange: (String) -> Unit,
    reminderEnabled: Boolean,
    reminderSummary: String?,
    onOpenReminderPicker: () -> Unit,
    onReminderDisabled: () -> Unit,
    onLoadTicket: () -> Unit,
    onBack: () -> Unit,
    onNextStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTicketSheet by rememberSaveable { mutableStateOf(false) }
    var datePickerTarget by rememberSaveable { mutableStateOf<String?>(null) }
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            TripHeader(onBack)
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StepHero(oneWay)
                QuickFillPanel {
                    onLoadTicket()
                    showTicketSheet = true
                }
                OneWayControl(oneWay, onOneWayChange)
                DateField(stringResource(R.string.trip_departure_date), departureDate, onDepartureDateChange) { datePickerTarget = "departure" }
                if (!oneWay) DateField(stringResource(R.string.trip_return_date), returnDate, onReturnDateChange) { datePickerTarget = "return" }
                ReminderControl(reminderEnabled, reminderSummary, onOpenReminderPicker, onReminderDisabled)
            }
            Footer(onBack, onNextStep, oneWay)
        }
        if (showTicketSheet) LoadTicketSheet { showTicketSheet = false }
        if (datePickerTarget != null) TripDatePickerSheet(
            title = if (datePickerTarget == "departure") stringResource(R.string.trip_departure_date) else stringResource(R.string.trip_return_date),
            onDismiss = { datePickerTarget = null },
            onSave = {
                if (datePickerTarget == "departure") onDepartureDateChange("2024-06-09") else onReturnDateChange("2024-06-09")
                datePickerTarget = null
            }
        )
    }
}

@Composable
private fun TripHeader(onBack: () -> Unit) {
    val secondary = Color(0xFF8D919A)
    Row(
        Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.c2g_arrow_left), stringResource(R.string.trip_back),
            colorFilter = ColorFilter.tint(secondary), modifier = Modifier.size(24.dp).clickable(onClick = onBack)
        )
        Spacer(Modifier.weight(1f))
        Text("Add a trip", color = Color(0xFF002349), fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        HeaderIcon(R.drawable.c2g_settings, secondary)
        Spacer(Modifier.width(10.dp)); HeaderIcon(R.drawable.c2g_help, secondary)
        Spacer(Modifier.width(10.dp)); HeaderIcon(R.drawable.c2g_notification, secondary)
    }
}

@Composable
private fun HeaderIcon(id: Int, tint: Color) {
    Image(painterResource(id), null, colorFilter = ColorFilter.tint(tint), modifier = Modifier.size(24.dp))
}

@Composable
private fun StepHero(oneWay: Boolean) {
    Box(Modifier.fillMaxWidth().height(89.dp).clip(RoundedCornerShape(12.dp))) {
        Image(painterResource(R.drawable.trip_airplane), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Black.copy(.58f), Color.Transparent))))
        Column(Modifier.align(Alignment.CenterStart).padding(start = 16.dp)) {
            Text(if (oneWay) "Step 1/2" else "Step 1/3", color = Color(0xFFF4F7FA), fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
            Text("Travel dates", color = Color(0xFFF4F7FA).copy(.8f), fontSize = 16.sp, lineHeight = 24.sp)
        }
    }
}

@Composable
private fun QuickFillPanel(onLoadTicket: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(146.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF4F7FA))) {
        Column(Modifier.padding(start = 16.dp, top = 16.dp)) {
            Text(stringResource(R.string.trip_quick_fill_title), color = Color(0xFF002349), fontSize = 16.sp, lineHeight = 24.sp)
            Text(
                "Upload your boarding pass to fill in\nyour flight details automatically",
                color = Color(0xFF8D919A), fontSize = 12.sp, lineHeight = 16.sp
            )
        }
        Image(
            painterResource(R.drawable.trip_boarding_pass), null, contentScale = ContentScale.Fit,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = 2.dp).size(width = 112.dp, height = 75.dp)
        )
        Box(
            Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth().height(43.dp)
                .clip(RoundedCornerShape(10.dp)).background(Color(0xFF043CB3)).clickable(onClick = onLoadTicket),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.c2g_upload_document), null, colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.trip_load_ticket), color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun OneWayControl(oneWay: Boolean, onChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(R.string.trip_one_way_label), color = Color(0xFF202427), fontSize = 16.sp, lineHeight = 24.sp)
        Row(Modifier.width(128.dp).height(42.dp).clip(RoundedCornerShape(9.dp)).background(Color.White).padding(3.dp)) {
            Choice(stringResource(R.string.trip_one_way_no), !oneWay, Modifier.weight(1f)) { onChange(false) }
            Choice(stringResource(R.string.trip_one_way_yes), oneWay, Modifier.weight(1f)) { onChange(true) }
        }
    }
}

@Composable
private fun Choice(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.fillMaxHeight().clip(RoundedCornerShape(7.dp)).background(if (selected) Color(0xFF043CB3) else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = if (selected) Color.White else Color(0xFF002349), fontSize = 14.sp)
    }
}

@Composable
private fun DateField(label: String, value: String, onChange: (String) -> Unit, onOpenPicker: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Color(0xFF002349), fontSize = 16.sp, lineHeight = 24.sp)
        Row(
            Modifier.fillMaxWidth().height(49.dp).clip(RoundedCornerShape(10.dp))
                .background(Color.White).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value, onChange, singleLine = true, modifier = Modifier.weight(1f),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF8D919A), fontSize = 16.sp, lineHeight = 24.sp),
                decorationBox = { inner -> if (value.isEmpty()) Text("25.03.2024", color = Color(0xFF8D919A), fontSize = 16.sp) else inner() }
            )
            Image(painterResource(R.drawable.c2g_events), label, colorFilter = ColorFilter.tint(Color(0xFF8D919A)), modifier = Modifier.size(24.dp).clickable(onClick = onOpenPicker).testTag(if (label.contains("Departure")) "departure_date_picker" else "return_date_picker"))
        }
    }
}

@Composable
private fun ReminderControl(enabled: Boolean, summary: String?, open: () -> Unit, disable: () -> Unit) {
    Column {
        Row(
            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF4F7FA))
                .clickable { if (enabled) disable() else open() }
                .testTag("trip_reminder_toggle")
                .semantics { toggleableState = ToggleableState(enabled) }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.trip_reminder_label), color = Color(0xFF202427), fontSize = 16.sp)
            Switch(
                checked = enabled, onCheckedChange = null,
                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF34C759), uncheckedTrackColor = Color(0xFFE5E5E6))
            )
        }
        if (enabled && summary != null) {
            Row(
                Modifier.fillMaxWidth().padding(start = 16.dp, top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(summary, color = Color(0xFF043CB3), fontSize = 12.sp)
                Text(stringResource(R.string.trip_reminder_edit), Modifier.clickable(onClick = open), color = Color(0xFF043CB3), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun Footer(onBack: () -> Unit, onNext: () -> Unit, oneWay: Boolean) {
    Column(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFE5E5E6))) {
            Box(Modifier.fillMaxWidth(if (oneWay) .5f else .33f).height(4.dp).background(Color(0xFFC14471)))
        }
        Text(if (oneWay) "Step 1/2. Travel dates" else "Step 1/3. Travel dates", Modifier.fillMaxWidth().padding(vertical = 5.dp), color = Color(0xFF8D919A), fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FooterButton(stringResource(R.string.trip_back), false, Modifier.weight(1f), onBack)
            FooterButton(stringResource(R.string.trip_next_step), true, Modifier.weight(1f), onNext)
        }
    }
}

@Composable
private fun FooterButton(label: String, primary: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.height(50.dp).clip(RoundedCornerShape(12.dp)).background(if (primary) Color(0xFF043CB3) else Color(0xFFEDF2F8)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = Color(0xFF043CB3).takeUnless { primary } ?: Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LoadTicketSheet(onDismiss: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0x99000000))) {
        Box(Modifier.fillMaxSize().clickable(onClick = onDismiss))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(467.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Color.White)
                .navigationBarsPadding().padding(16.dp)
        ) {
            Text("Load your ticket", Modifier.fillMaxWidth(), color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(Modifier.height(25.dp)); TicketSheetAction("▧", "Choose from gallery")
            Spacer(Modifier.height(18.dp)); TicketSheetAction("⇥", "Choose from files")
            Text("Recently downloaded:", Modifier.padding(top = 32.dp, bottom = 8.dp), color = Color(0xFF8D919A), fontSize = 16.sp)
            listOf("Ticket 1. Moscow–Tel Aviv.pdf", "image_2024-0... Moscow-Tel Aviv.png", "Ticket 1. Moscow–Tel Aviv.pdf", "Ticket 1. Moscow–Tel Aviv.pdf").forEach {
                Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.c2g_upload_document), null, Modifier.size(44.dp).clip(RoundedCornerShape(4.dp)), colorFilter = ColorFilter.tint(Color(0xFF8D919A)))
                    Column(Modifier.padding(start = 12.dp)) { Text(it, color = Color(0xFF002349), fontSize = 15.sp); Text("1MB", color = Color(0xFF8D919A), fontSize = 15.sp) }
                }
            }
        }
    }
}

@Composable
private fun TicketSheetAction(icon: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) { Text(icon, Modifier.width(32.dp), color = Color(0xFF002349), fontSize = 21.sp); Text(label, color = Color(0xFF002349), fontSize = 17.sp) }
}

@Composable
private fun TripDatePickerSheet(title: String, onDismiss: () -> Unit, onSave: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0x99000000))) {
        Box(Modifier.fillMaxSize().clickable(onClick = onDismiss))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(546.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Color.White)
                .navigationBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))
            Column(Modifier.fillMaxWidth().height(314.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFCFCFD))) {
                Row(Modifier.fillMaxWidth().height(34.dp).background(Color(0xFFF8F8F9)), verticalAlignment = Alignment.CenterVertically) {
                    listOf("SUN","MON","TUE","WED","THU","FRI","SAT").forEach { Text(it, Modifier.weight(1f), color = Color(0xFFC3C4C7), fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
                }
                Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("June 2024", color = Color(0xFF002349), fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Text("  ›", color = Color(0xFF043CB3), fontSize = 24.sp); Spacer(Modifier.weight(1f)); Text("‹    ›", color = Color(0xFF043CB3), fontSize = 24.sp)
                }
                val weeks = listOf(listOf<Int?>(null,null,null,null,null,null,1), listOf(2,3,4,5,6,7,8), listOf(9,10,11,12,13,14,15), listOf(16,17,18,19,20,21,22), listOf(23,24,25,26,27,28,29), listOf(30,null,null,null,null,null,null))
                weeks.forEach { week -> Row(Modifier.fillMaxWidth().weight(1f)) { week.forEach { day -> Box(Modifier.weight(1f).fillMaxHeight().background(if (day == 9) Color(0xFF043CB3) else Color.Transparent), contentAlignment = Alignment.Center) { if (day != null) Text(day.toString(), color = if (day == 9) Color.White else Color(0xFF002349), fontSize = 16.sp) } } } }
            }
            Text("Fri, 9 June", Modifier.padding(top = 24.dp), color = Color(0xFF043CB3), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Box(Modifier.padding(top = 24.dp).fillMaxWidth().height(50.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF043CB3)).clickable(onClick = onSave).testTag("save_trip_date"), contentAlignment = Alignment.Center) { Text("Save date", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewDates() {
    Check2GoTheme(false) { TripCreateDatesScreen(false, {}, "", {}, "", {}, false, null, {}, {}, {}, {}, {}) }
}
