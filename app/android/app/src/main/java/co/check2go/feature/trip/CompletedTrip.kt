package co.check2go.feature.trip

import androidx.compose.runtime.saveable.Saver

/**
 * Smallest useful local summary of a trip that finished the Add Trip flow (TRIP_CREATE_TRAVELERS
 * "Complete"), shown as a card/row on MY_TRIPS. Local-only: no Room/backend persistence, see
 * docs/android/architecture.md "Data direction".
 */
data class CompletedTrip(
    val id: Long,
    val tripName: String,
    val destinationCountry: String,
    val departureCountry: String,
    val oneWay: Boolean,
    val departureDate: String,
    val returnDate: String
)

/** Filter tabs verified on the MY_TRIPS screen (docs/screen-inventory.md, "My trips — list view"). */
enum class TripFilter { Active, All }

// A control character rather than a normal punctuation mark, so it cannot collide with
// user-entered trip/country text.
private val FIELD_SEPARATOR: String = Char(1).toString()

/**
 * Encodes each [CompletedTrip] as a delimited string so the completed-trip list can survive
 * recomposition/configuration changes via `rememberSaveable` without adding a Parcelize
 * dependency. This is a saveable-state representation only, not durable persistence: the list is
 * lost on process death / fresh app relaunch.
 */
val CompletedTripListSaver: Saver<List<CompletedTrip>, ArrayList<String>> = Saver(
    save = { trips -> ArrayList(trips.map { it.encode() }) },
    restore = { encoded -> encoded.map { it.decode() } }
)

private fun CompletedTrip.encode(): String = listOf(
    id.toString(),
    tripName,
    destinationCountry,
    departureCountry,
    oneWay.toString(),
    departureDate,
    returnDate
).joinToString(FIELD_SEPARATOR)

private fun String.decode(): CompletedTrip {
    val fields = split(FIELD_SEPARATOR)
    return CompletedTrip(
        id = fields[0].toLong(),
        tripName = fields[1],
        destinationCountry = fields[2],
        departureCountry = fields[3],
        oneWay = fields[4].toBoolean(),
        departureDate = fields[5],
        returnDate = fields[6]
    )
}
