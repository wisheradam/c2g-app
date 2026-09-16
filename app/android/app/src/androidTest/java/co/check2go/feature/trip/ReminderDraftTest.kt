package co.check2go.feature.trip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-transformation tests for the REMINDER_PICKER draft model (docs/flows.md Flow 10): no
 * Compose involved, so these run as plain JUnit assertions (this module has no JVM `src/test`
 * source set, see build.gradle.kts, so they live alongside the other feature tests under
 * androidTest -- see [co.check2go.feature.checklists.CompletedChecklistTest]'s doc comment).
 */
class ReminderDraftTest {

    @Test
    fun parsedDateReturnsTheDateForAValidIsoDate() {
        val draft = ReminderDraft(date = "2026-10-01", time = "")

        assertEquals(java.time.LocalDate.of(2026, 10, 1), draft.parsedDate())
    }

    @Test
    fun parsedDateReturnsNullForAnEmptyDate() {
        assertNull(ReminderDraft(date = "", time = "").parsedDate())
    }

    @Test
    fun parsedDateReturnsNullForAMalformedDate() {
        assertNull(ReminderDraft(date = "2026/10/01", time = "").parsedDate())
    }

    @Test
    fun parsedDateReturnsNullForACalendarInvalidDate() {
        // 2026 is not a leap year, so February only has 28 days.
        assertNull(ReminderDraft(date = "2026-02-29", time = "").parsedDate())
    }

    @Test
    fun parsedDateAcceptsALeapYearFebruary29() {
        assertEquals(java.time.LocalDate.of(2028, 2, 29), ReminderDraft(date = "2028-02-29", time = "").parsedDate())
    }

    @Test
    fun parsedTimeReturnsTheTimeForAValid24HourTime() {
        assertEquals(java.time.LocalTime.of(9, 30), ReminderDraft(date = "", time = "09:30").parsedTime())
    }

    @Test
    fun parsedTimeAcceptsMidnight() {
        assertEquals(java.time.LocalTime.of(0, 0), ReminderDraft(date = "", time = "00:00").parsedTime())
    }

    @Test
    fun parsedTimeReturnsNullForAnEmptyTime() {
        assertNull(ReminderDraft(date = "", time = "").parsedTime())
    }

    @Test
    fun parsedTimeReturnsNullForAnOutOfRangeHour() {
        assertNull(ReminderDraft(date = "", time = "24:00").parsedTime())
    }

    @Test
    fun parsedTimeReturnsNullForAMalformedTime() {
        assertNull(ReminderDraft(date = "", time = "9:30").parsedTime())
    }

    @Test
    fun isValidIsTrueOnlyWhenBothDateAndTimeParse() {
        assertTrue(ReminderDraft(date = "2026-10-01", time = "09:30").isValid())
        assertFalse(ReminderDraft(date = "2026-10-01", time = "").isValid())
        assertFalse(ReminderDraft(date = "", time = "09:30").isValid())
        assertFalse(ReminderDraft(date = "", time = "").isValid())
        assertFalse(ReminderDraft(date = "not-a-date", time = "09:30").isValid())
        assertFalse(ReminderDraft(date = "2026-10-01", time = "not-a-time").isValid())
    }
}
