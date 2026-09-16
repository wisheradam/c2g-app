package co.check2go.feature.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.feature.checklists.TeamChecklistsPromo

/** Approved long-form Trip Hub variants 1:29340, 1:29482, 1:29624 and 1:29766. */
@Composable
fun TripHubScreen(trip: CompletedTrip, onBack: () -> Unit, onCreateChecklist: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF4F7FA))) {
        TripHubHeader(trip.tripName, onBack)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            TripHubHero(trip)
            QuickServices()
            Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                ChecklistSection(onCreateChecklist)
                HubSection("Routes in the footsteps of famous\nbloggers") { RouteCards() }
                HubSection("Events Calendar", "In and around the place of stay") { EventCards() }
                HubSection("Documents", "Keep your tickets always at hand") { DocumentCards() }
                TeamChecklistsPromo()
                HubSection("What to see", "In the vicinity of your location") { PlaceCards() }
                ServiceCard("Book accommodation\nat the best prices", "Booking     airbnb", "Request accommodation selection", Color(0xFFFFFDF2))
                ServiceCard("Order a taxi and travel\nanywhere", "YANGO     Gett.", "Request taxi", Color(0xFFF7FFF1))
                ServiceCard("Don't think about\nmoving", "Airport transfer", "Book a transfer", Color(0xFFFFE9D1))
                ServiceCard("Move on your own\nto all the sights", "SIXT     AVIS", "Request for car rental", Color(0xFFFFFDF7))
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable private fun TripHubHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(76.dp).background(Color.White).statusBarsPadding().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.c2g_arrow_left), "Back", Modifier.size(20.dp).clickable(onClick = onBack), colorFilter = ColorFilter.tint(Color(0xFF8D919A)))
        Text(title, Modifier.weight(1f), color = Color(0xFF002349), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        listOf(R.drawable.c2g_settings, R.drawable.c2g_help, R.drawable.c2g_notification).forEach { Image(painterResource(it), null, Modifier.padding(start = 10.dp).size(19.dp), colorFilter = ColorFilter.tint(Color(0xFF8D919A))) }
    }
}

@Composable private fun TripHubHero(trip: CompletedTrip) {
    Box(Modifier.fillMaxWidth().height(145.dp)) {
        Image(painterResource(R.drawable.trip_city_tel_aviv), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.65f)))))
        Text("Active", Modifier.padding(10.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD5F7FA)).padding(horizontal = 8.dp, vertical = 3.dp), color = Color(0xFF007B83), fontSize = 11.sp)
        Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) { Text(trip.tripName, color = Color.White, fontSize = 16.sp); Text(trip.departureDate, color = Color.White, fontSize = 13.sp) }
    }
}

@Composable private fun QuickServices() { Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Accommodation","Taxi","Rent a Car","My smart home").forEach { Text(it, Modifier.clip(RoundedCornerShape(18.dp)).background(Color(0xFFE5ECF6)).padding(horizontal = 14.dp, vertical = 8.dp), color = Color(0xFF043CB3), fontSize = 13.sp) } } }

@Composable private fun ChecklistSection(onCreate: () -> Unit) {
    Text("Checklists", color = Color(0xFF002349), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    listOf("Transfer" to 25, "Accommodation" to 16, "Documents" to 0, "Take on a trip" to 100).forEach { (name, pct) ->
        Row(Modifier.fillMaxWidth().height(49.dp).clip(RoundedCornerShape(10.dp)).background(if (pct == 100) Color(0xFFD5ECE8) else Color.White).padding(10.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(name, color = Color(0xFF002349), fontSize = 14.sp); Text("London–Tel-Aviv", color = Color(0xFF8D919A), fontSize = 10.sp) }; Text("$pct% complete", color = Color(0xFF043CB3), fontSize = 11.sp) }
        Spacer(Modifier.height(6.dp))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { HubButton("Add from the list", false, Modifier.weight(1f), {}); HubButton("Create new one", true, Modifier.weight(1f), onCreate) }
}

@Composable private fun HubSection(title: String, subtitle: String? = null, content: @Composable () -> Unit) { Column { Text(title, color = Color(0xFF002349), fontSize = 18.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold); if (subtitle != null) Text(subtitle, color = Color(0xFF002349), fontSize = 13.sp); Spacer(Modifier.height(10.dp)); content() } }

@Composable private fun RouteCards() { Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { repeat(3) { Column(Modifier.width(138.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).padding(7.dp)) { Image(painterResource(R.drawable.traveler_avatar_generated), null, Modifier.fillMaxWidth().height(102.dp), contentScale = ContentScale.Fit); Text("Travel routes", color = Color(0xFF002349), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) } } } }
@Composable private fun EventCards() { Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Shavuot holiday","International Self Care Day","Tisha B'Av").forEach { Column(Modifier.width(118.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).padding(6.dp)) { Image(painterResource(R.drawable.trip_city_tel_aviv), null, Modifier.fillMaxWidth().height(96.dp), contentScale = ContentScale.Crop); Text("08.07.2024", color = Color(0xFF043CB3), fontSize = 10.sp); Text(it, color = Color(0xFF002349), fontSize = 12.sp, maxLines = 2) } } } }
@Composable private fun DocumentCards() { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Documents\n10 files","Tickets\n2 files").forEach { Text(it, Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(Color.White).padding(12.dp), color = Color(0xFF002349), fontSize = 12.sp) } } }
@Composable private fun PlaceCards() { Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Restaurants and bars","Famous places","Photo locations").forEach { Column(Modifier.width(118.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).padding(6.dp)) { Image(painterResource(R.drawable.trip_city_tel_aviv), null, Modifier.fillMaxWidth().height(96.dp), contentScale = ContentScale.Crop); Text(it, color = Color(0xFF002349), fontSize = 12.sp) } } } }

@Composable private fun ServiceCard(title: String, providers: String, action: String, color: Color) { Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(color).padding(14.dp)) { Text(title, color = Color(0xFF002349), fontSize = 18.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold); Text(providers, Modifier.padding(vertical = 16.dp), color = Color(0xFF043CB3), fontSize = 20.sp, fontWeight = FontWeight.Bold); HubButton(action, false, Modifier.fillMaxWidth(), {}) } }
@Composable private fun HubButton(label: String, primary: Boolean, modifier: Modifier, onClick: () -> Unit) { Box(modifier.height(40.dp).clip(RoundedCornerShape(8.dp)).background(if (primary) Color(0xFF043CB3) else Color(0xFFE6EDF5)).clickable(onClick = onClick), contentAlignment = Alignment.Center) { Text(label, color = if (primary) Color.White else Color(0xFF043CB3), fontSize = 14.sp) } }
