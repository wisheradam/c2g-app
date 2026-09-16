package co.check2go.feature.trip

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

/**
 * Draft captured by the shared REMINDER_PICKER screen (docs/flows.md Flow 10;
 * docs/screen-inventory.md "Reminder / Date-Time Picker"). [date]/[time] are temporary ISO-8601
 * (`YYYY-MM-DD`) / 24-hour (`HH:mm`) text input until the Figma calendar/time picker controls are
 * recovered (docs/screen-inventory.md "Date picker bottom sheet"). Parsing is pure and local only:
 * no timezone conversion or OS notification/scheduling semantics are attached to this value.
 */
data class ReminderDraft(
    val date: String = "",
    val time: String = ""
)

/** Parses [ReminderDraft.date] as a calendar date, or `null` if blank/not a valid ISO-8601 date. */
fun ReminderDraft.parsedDate(): LocalDate? = try {
    LocalDate.parse(date)
} catch (_: DateTimeParseException) {
    null
}

/** Parses [ReminderDraft.time] as a 24-hour local time, or `null` if blank/not a valid `HH:mm` time. */
fun ReminderDraft.parsedTime(): LocalTime? = try {
    LocalTime.parse(time)
} catch (_: DateTimeParseException) {
    null
}

/**
 * True once both [ReminderDraft.date] and [ReminderDraft.time] parse to valid values. The
 * REMINDER_PICKER "Set a reminder" action stays disabled until this is true (Flow 10, step 5).
 */
fun ReminderDraft.isValid(): Boolean = parsedDate() != null && parsedTime() != null
