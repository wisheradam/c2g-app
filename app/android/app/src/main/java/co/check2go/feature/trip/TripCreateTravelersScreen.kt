package co.check2go.feature.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

enum class TripAdventureType { Solo, Group }

data class TripTravelersDraft(val adventureType: TripAdventureType, val petsIncluded: Boolean)

/** Approved Figma traveler states 1:29178, 1:29204, 1:29236 and 1:29269. */
@Composable
fun TripCreateTravelersScreen(
    adventureType: TripAdventureType,
    onAdventureTypeChange: (TripAdventureType) -> Unit,
    petsIncluded: Boolean,
    onPetsIncludedChange: (Boolean) -> Unit,
    onAddTraveler: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TravelersHeader(onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            TravelersHero()
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.trip_create_travelers_title), color = Color(0xFF002349), fontSize = 16.sp)
                Row(Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(9.dp)).background(Color.White).padding(3.dp)) {
                    AdventureChoice(stringResource(R.string.trip_solo_adventure), adventureType == TripAdventureType.Solo, Modifier.weight(1f)) { onAdventureTypeChange(TripAdventureType.Solo) }
                    AdventureChoice(stringResource(R.string.trip_group_adventure), adventureType == TripAdventureType.Group, Modifier.weight(1f)) { onAdventureTypeChange(TripAdventureType.Group) }
                }
            }
            if (adventureType == TripAdventureType.Group) MyTravelersPanel(onAddTraveler)
            PetsChoice(petsIncluded, onPetsIncludedChange)
        }
        TravelersFooter(onBack, onComplete)
    }
}

@Composable
private fun TravelersHeader(onBack: () -> Unit) {
    val secondary = Color(0xFF8D919A)
    Row(Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.c2g_arrow_left), stringResource(R.string.trip_back), colorFilter = ColorFilter.tint(secondary), modifier = Modifier.size(24.dp).clickable(onClick = onBack))
        Spacer(Modifier.weight(1f)); Text("Add a trip", color = Color(0xFF002349), fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f))
        SmallHeaderIcon(R.drawable.c2g_settings, secondary); Spacer(Modifier.width(10.dp)); SmallHeaderIcon(R.drawable.c2g_help, secondary); Spacer(Modifier.width(10.dp)); SmallHeaderIcon(R.drawable.c2g_notification, secondary)
    }
}

@Composable private fun SmallHeaderIcon(id: Int, tint: Color) = Image(painterResource(id), null, colorFilter = ColorFilter.tint(tint), modifier = Modifier.size(24.dp))

@Composable
private fun TravelersHero() {
    Box(Modifier.fillMaxWidth().height(89.dp).clip(RoundedCornerShape(12.dp))) {
        Image(painterResource(R.drawable.trip_travelers), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Black.copy(.55f), Color.Transparent))))
        Column(Modifier.align(Alignment.CenterStart).padding(start = 16.dp)) {
            Text("Step 2/2", color = Color(0xFFF4F7FA), fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
            Text("Travelers", color = Color(0xFFF4F7FA).copy(.8f), fontSize = 16.sp, lineHeight = 24.sp)
        }
    }
}

@Composable
private fun AdventureChoice(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.fillMaxHeight().clip(RoundedCornerShape(7.dp)).background(if (selected) Color(0xFF043CB3) else Color.Transparent).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = if (selected) Color.White else Color(0xFF002349), fontSize = 14.sp)
    }
}

@Composable
private fun MyTravelersPanel(onAddTraveler: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.trip_my_travelers_title), color = Color(0xFF002349), fontSize = 16.sp)
        Box(
            Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFEDF2F8)).clickable(onClick = onAddTraveler),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("＋", color = Color(0xFF043CB3), fontSize = 20.sp)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.trip_add_traveler), color = Color(0xFF043CB3), fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun PetsChoice(included: Boolean, onChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(R.string.trip_pets_label), color = Color(0xFF202427), fontSize = 16.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            RadioChoice(stringResource(R.string.trip_pets_yes), included) { onChange(true) }
            RadioChoice(stringResource(R.string.trip_pets_no), !included) { onChange(false) }
        }
    }
}

@Composable
private fun RadioChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(22.dp).background(Color.White, CircleShape).padding(3.dp).then(if (selected) Modifier.background(Color(0xFFC14471), CircleShape) else Modifier))
        Spacer(Modifier.width(8.dp)); Text(label, color = Color(0xFF002349), fontSize = 16.sp)
    }
}

@Composable
private fun TravelersFooter(onBack: () -> Unit, onComplete: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF20D463)))
        Text("Step 2/2. Travelers", Modifier.fillMaxWidth().padding(vertical = 5.dp), color = Color(0xFF8D919A), fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TravelerFooterButton(stringResource(R.string.trip_back), false, Modifier.weight(1f), onBack)
            TravelerFooterButton(stringResource(R.string.trip_complete), true, Modifier.weight(1f), onComplete)
        }
    }
}

@Composable
private fun TravelerFooterButton(label: String, primary: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.height(50.dp).clip(RoundedCornerShape(12.dp)).background(if (primary) Color(0xFF043CB3) else Color(0xFFEDF2F8)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = if (primary) Color.White else Color(0xFF043CB3), fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun TravelersPreview() = Check2GoTheme(false) { TripCreateTravelersScreen(TripAdventureType.Group, {}, false, {}, {}, {}, {}) }
