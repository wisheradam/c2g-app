package co.check2go.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import co.check2go.feature.trip.CompletedTrip
import co.check2go.feature.trip.TripFilter
import co.check2go.ui.theme.Check2GoTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MyTripsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val summerTrip = CompletedTrip(
        id = 1L,
        tripName = "Summer trip",
        destinationCountry = "Italy",
        departureCountry = "Israel",
        oneWay = false,
        departureDate = "2026-10-01",
        returnDate = "2026-10-10"
    )

    @Test
    fun layoutToggleSwitchesBetweenApprovedGridAndListStates() {
        composeRule.setContent {
            Check2GoTheme {
                MyTripsScreen(
                    trips = listOf(summerTrip), filter = TripFilter.Active,
                    onFilterChange = {}, onAddTrip = {}, onQuickAdd = {}, onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithTag("trip_grid").assertIsDisplayed()
        composeRule.onNodeWithTag("trip_layout_toggle").performClick()
        composeRule.onNodeWithTag("trip_list").assertIsDisplayed()
    }

    @Test
    fun cardShowsTripNameRouteAndDates() {
        composeRule.setContent {
            Check2GoTheme {
                MyTripsScreen(
                    trips = listOf(summerTrip),
                    filter = TripFilter.Active,
                    onFilterChange = {},
                    onAddTrip = {},
                    onQuickAdd = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("My trips").assertIsDisplayed()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
        composeRule.onNodeWithText("Israel → Italy").assertIsDisplayed()
        composeRule.onNodeWithText("2026-10-01 – 2026-10-10").assertIsDisplayed()
    }

    @Test
    fun oneWayTripShowsSingleDateAndNeutralStatus() {
        composeRule.setContent {
            Check2GoTheme {
                MyTripsScreen(
                    trips = listOf(summerTrip.copy(oneWay = true, returnDate = "")),
                    filter = TripFilter.Active,
                    onFilterChange = {},
                    onAddTrip = {},
                    onQuickAdd = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("2026-10-01").assertIsDisplayed()
        composeRule.onNodeWithText("Trip added").assertIsDisplayed()
    }

    @Test
    fun bothFilterTabsShowCompletedTripsUntilArchivingExists() {
        composeRule.setContent {
            Check2GoTheme {
                var filter by remember { mutableStateOf(TripFilter.Active) }
                MyTripsScreen(
                    trips = listOf(summerTrip),
                    filter = filter,
                    onFilterChange = { filter = it },
                    onAddTrip = {},
                    onQuickAdd = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("All").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()

        composeRule.onNodeWithText("Active").performClick()
        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
    }

    @Test
    fun multipleCompletedTripsAreAllRetainedAndVisible() {
        val secondTrip = CompletedTrip(
            id = 2L,
            tripName = "Winter escape",
            destinationCountry = "Norway",
            departureCountry = "Spain",
            oneWay = false,
            departureDate = "2026-12-05",
            returnDate = "2026-12-12"
        )

        composeRule.setContent {
            Check2GoTheme {
                MyTripsScreen(
                    trips = listOf(summerTrip, secondTrip),
                    filter = TripFilter.All,
                    onFilterChange = {},
                    onAddTrip = {},
                    onQuickAdd = {},
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Summer trip").assertIsDisplayed()
        composeRule.onNodeWithText("Winter escape").assertIsDisplayed()
    }

    @Test
    fun addTripAndQuickAddEmitCallbacks() {
        var addTripRequested = false
        var quickAddRequested = false

        composeRule.setContent {
            Check2GoTheme {
                MyTripsScreen(
                    trips = listOf(summerTrip),
                    filter = TripFilter.Active,
                    onFilterChange = {},
                    onAddTrip = { addTripRequested = true },
                    onQuickAdd = { quickAddRequested = true },
                    onDestinationSelected = {}
                )
            }
        }

        composeRule.onNodeWithText("Add a trip").performClick()
        composeRule.onNodeWithContentDescription("Quick add").performClick()

        assertTrue(addTripRequested)
        assertTrue(quickAddRequested)
    }
}
