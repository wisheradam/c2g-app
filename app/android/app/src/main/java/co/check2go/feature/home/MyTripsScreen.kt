package co.check2go.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppHeader
import co.check2go.core.design.AppNavigationBar
import co.check2go.feature.trip.CompletedTrip
import co.check2go.feature.trip.TripFilter
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma HOME_TRIPS grid frame 1:28448. */
@Composable
fun MyTripsScreen(
    trips: List<CompletedTrip>,
    filter: TripFilter,
    onFilterChange: (TripFilter) -> Unit,
    onAddTrip: () -> Unit,
    onQuickAdd: () -> Unit,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    onTripSelected: (Long) -> Unit = {},
    onAccountClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = false
) {
    var gridLayout by rememberSaveable { mutableStateOf(true) }
    val quickAddLabel = stringResource(R.string.home_quick_add)
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            AppHeader(onAccountClick, onSettingsClick, onToggleTheme, isDarkTheme)
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp).weight(1f)) {
                Row(Modifier.fillMaxWidth().height(34.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.trip_my_trips_title),
                        color = Color(0xFF002349), fontSize = 20.sp, lineHeight = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    FilterControl(filter, onFilterChange)
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(Color.White)
                            .clickable { gridLayout = !gridLayout }.testTag("trip_layout_toggle"),
                        contentAlignment = Alignment.Center
                    ) { Text(if (gridLayout) "☰" else "▦", color = Color(0xFF043CB3), fontSize = 20.sp) }
                }
                if (gridLayout) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).heightIn(max = 316.dp)
                            .testTag("trip_grid")
                    ) {
                        items(trips, key = { it.id }) { TripCard(it) { onTripSelected(it.id) } }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).heightIn(max = 316.dp)
                            .testTag("trip_list")
                    ) {
                        lazyItems(trips, key = { it.id }) { trip ->
                            TripListCard(trip) { onTripSelected(trip.id) }
                        }
                    }
                }
                Box(
                    Modifier.padding(top = 16.dp).fillMaxWidth().height(52.dp)
                        .clip(RoundedCornerShape(12.dp)).background(Color(0xFF043CB3))
                        .clickable(role = Role.Button, onClick = onAddTrip),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.home_add_trip), color = Color.White, fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium)
                }
            }
            AppNavigationBar(AppDestination.Home, onDestinationSelected)
        }
        Box(
            Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(end = 8.dp, bottom = 74.dp)
                .size(56.dp).background(Color(0xFF043CB3), CircleShape)
                .semantics { contentDescription = quickAddLabel; role = Role.Button }
                .clickable(onClick = onQuickAdd), contentAlignment = Alignment.Center
        ) { Text("+", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Light) }
    }
}

/** Approved Figma one-column trip card states 1:27854, 1:27885 and 1:27916. */
@Composable
private fun TripListCard(trip: CompletedTrip, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(154.dp).clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painterResource(R.drawable.trip_city_tel_aviv),
            null,
            Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = .72f)),
                    startY = 55f
                )
            )
        )
        Text(
            stringResource(R.string.trip_filter_active),
            Modifier.padding(16.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD5F7FA))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFF007B83), fontSize = 12.sp
        )
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(trip.tripName, color = Color.White, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(trip.datesLabel(), color = Color.White, fontSize = 14.sp)
        }
        Box(
            Modifier.align(Alignment.BottomEnd).padding(16.dp).size(36.dp)
                .clip(RoundedCornerShape(10.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) { Text("›", color = Color(0xFF043CB3), fontSize = 25.sp) }
    }
}

@Composable
private fun FilterControl(filter: TripFilter, onFilterChange: (TripFilter) -> Unit) {
    Row(Modifier.width(140.dp).height(32.dp).clip(RoundedCornerShape(9.dp)).background(Color.White).padding(2.dp)) {
        FilterButton(stringResource(R.string.trip_filter_active), filter == TripFilter.Active, Modifier.weight(1f).testTag("trip_filter_active")) { onFilterChange(TripFilter.Active) }
        FilterButton(stringResource(R.string.trip_filter_all), filter == TripFilter.All, Modifier.weight(1f).testTag("trip_filter_all")) { onFilterChange(TripFilter.All) }
    }
}

@Composable
private fun FilterButton(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.height(28.dp).clip(RoundedCornerShape(7.dp))
            .background(if (selected) Color(0xFF043CB3) else Color.Transparent).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(label, color = if (selected) Color.White else Color(0xFF002349), fontSize = 14.sp, lineHeight = 16.sp) }
}

@Composable
private fun TripCard(trip: CompletedTrip, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(154.dp).clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick)) {
        Image(painterResource(R.drawable.trip_city_tel_aviv), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .75f)), startY = 55f)))
        Text(
            stringResource(R.string.trip_filter_active),
            Modifier.padding(10.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD5F7FA)).padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFF007B83), fontSize = 12.sp, lineHeight = 16.sp
        )
        Column(Modifier.align(Alignment.BottomStart).padding(10.dp)) {
            Text(trip.tripName, color = Color.White, fontSize = 16.sp, lineHeight = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                stringResource(R.string.trip_route_format, trip.departureCountry, trip.destinationCountry),
                color = Color.White, fontSize = 12.sp, lineHeight = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(trip.datesLabel(), color = Color.White, fontSize = 12.sp, lineHeight = 15.sp)
            Text(stringResource(R.string.trip_status_added), color = Color.Transparent, fontSize = 1.sp, lineHeight = 1.sp)
        }
    }
}

@Composable
private fun CompletedTrip.datesLabel(): String = when {
    departureDate.isBlank() && (oneWay || returnDate.isBlank()) -> stringResource(R.string.trip_dates_not_set)
    oneWay || returnDate.isBlank() -> departureDate
    else -> stringResource(R.string.trip_dates_round_trip_format, departureDate, returnDate)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewMyTrips() {
    Check2GoTheme(false) {
        MyTripsScreen(
            listOf(CompletedTrip(1, "London - Tel Aviv", "Tel Aviv", "London", false, "25.03.2024", "06.04.2024")),
            TripFilter.All, {}, {}, {}, {}
        )
    }
}
