package co.check2go.feature.account

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class LocalAccountStoreTest {
    @Test
    fun encryptedStoreRoundTripsCompleteAccountWithoutPlaintext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalAccountStore(context)
        store.clear()
        val account = PersonalAccount(
            name = PersonName("PrivateFirstName", "PrivateLastName", "Middle"),
            gender = Gender(GenderOption.Other, "Custom"),
            dateOfBirth = LocalDate.of(1990, 2, 3),
            address = PostalAddress("Line 1", "Line 2", "City", "Region", "123", "IL"),
            citizenshipCountryCodes = setOf("IL", "US"),
            residence = Residence("IL", "Tel Aviv", ResidencyStatus.Citizen),
            residencePermits = listOf(ResidencePermit("p1", "US", ResidencyStatus.VisaHolder, "B1", LocalDate.of(2025, 1, 1), LocalDate.of(2027, 1, 1))),
            entryRestrictions = listOf(EntryRestriction("r1", "XY", EntryRestrictionType.TravelBan, "Details")),
            familyMembers = listOf(
                FamilyMember("f1", PersonName("Family", "Member"), LocalDate.of(2010, 4, 5), Gender(GenderOption.Female), setOf("IL"), "IL", Relationship(RelationshipType.Child))
            ),
            interests = listOf(Interest("music", "Music")),
            consents = listOf(ConsentRecord(ConsentType.PrivacyPolicy, listOf(ConsentDecision(ConsentStatus.Accepted, Instant.parse("2026-01-01T00:00:00Z"), "1.0")))),
            deletionRequest = AccountDeletionRequest(Instant.parse("2026-02-01T00:00:00Z"))
        )

        store.save(account)

        assertEquals(account, LocalAccountStore(context).load())
        val raw = context.getSharedPreferences("personal_account_device_only", Context.MODE_PRIVATE)
            .getString("encrypted_account_v1", "").orEmpty()
        assertFalse(raw.contains("PrivateFirstName"))
        assertFalse(raw.contains("Tel Aviv"))
        store.clear()
    }
}
