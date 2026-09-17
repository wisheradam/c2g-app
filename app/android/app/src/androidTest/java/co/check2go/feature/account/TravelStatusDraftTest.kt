package co.check2go.feature.account

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TravelStatusDraftTest {
    @Test fun permitMapsDatesAndCountry() {
        val draft = ResidencePermitDraft("il", ResidencyStatus.PermanentResident, "B/1", "2025-01-02", "2027-01-02")
        assertNull(draft.validationError())
        val permit = draft.toPermit("p1")
        assertEquals("IL", permit.countryCode)
        assertEquals(LocalDate.of(2027, 1, 2), permit.validUntil)
    }

    @Test fun restrictionRequiresDetailsAndValidDate() {
        assertEquals("Details are required", EntryRestrictionDraft(countryCode = "US").validationError())
        assertEquals("Use date format YYYY-MM-DD", EntryRestrictionDraft("US", details = "Restriction", effectiveFrom = "bad").validationError())
    }
}
