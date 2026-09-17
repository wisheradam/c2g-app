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
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            TripHeader(onBack)
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StepHero(oneWay)
                QuickFillPanel(onLoadTicket)
                OneWayControl(oneWay, onOneWayChange)
                DateField(stringResource(R.string.trip_departure_date), departureDate, onDepartureDateChange)
                if (!oneWay) DateField(stringResource(R.string.trip_return_date), returnDate, onReturnDateChange)
                ReminderControl(reminderEnabled, reminderSummary, onOpenReminderPicker, onReminderDisabled)
            }
            Footer(onBack, onNextStep, oneWay)
        }
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
private fun DateField(label: String, value: String, onChange: (String) -> Unit) {
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
            Image(painterResource(R.drawable.c2g_events), null, colorFilter = ColorFilter.tint(Color(0xFF8D919A)), modifier = Modifier.size(24.dp))
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

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewDates() {
    Check2GoTheme(false) { TripCreateDatesScreen(false, {}, "", {}, "", {}, false, null, {}, {}, {}, {}, {}) }
}
