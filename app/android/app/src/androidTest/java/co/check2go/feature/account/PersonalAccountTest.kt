package co.check2go.feature.account

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalAccountTest {
    @Test
    fun ageIsDerivedFromBirthDateInsteadOfStoredAsDuplicatedMutableData() {
        val account = PersonalAccount(dateOfBirth = LocalDate.of(1990, 10, 20))

        assertEquals(34, account.ageOn(LocalDate.of(2025, 10, 19)))
        assertEquals(35, account.ageOn(LocalDate.of(2025, 10, 20)))
        assertNull(account.ageOn(LocalDate.of(1989, 1, 1)))
    }

    @Test
    fun multipleCitizenshipsAreRepresentedWithoutDuplicates() {
        val account = PersonalAccount(citizenshipCountryCodes = setOf("IL", "FR", "IL"))

        assertEquals(setOf("IL", "FR"), account.citizenshipCountryCodes)
    }

    @Test
    fun familyMembersCanBeAddedAndRemovedIndependently() {
        val member = FamilyMember(
            id = "family-1",
            name = PersonName(firstName = "Alex", lastName = "Lee"),
            relationship = Relationship(RelationshipType.Child)
        )

        val added = PersonalAccount().withFamilyMemberAdded(member).withFamilyMemberAdded(member)
        assertEquals(1, added.familyMembers.size)
        assertTrue(added.withFamilyMemberRemoved("family-1").familyMembers.isEmpty())
    }

    @Test
    fun otherRelationshipRequiresManualValue() {
        assertThrows(IllegalArgumentException::class.java) {
            Relationship(RelationshipType.Other)
        }

        assertEquals("Cousin", Relationship(RelationshipType.Other, "Cousin").customLabel)
    }

    @Test
    fun interestsCanBeAddedAndRemovedAsSeparateItems() {
        val account = PersonalAccount()
            .withInterestAdded(Interest("hiking", "Hiking"))
            .withInterestAdded(Interest("food", "Local food"))
            .withInterestRemoved("hiking")

        assertEquals(listOf("Local food"), account.interests.map { it.label })
    }

    @Test
    fun consentHistoryRetainsStatusTimestampAndTextVersion() {
        val acceptedAt = Instant.parse("2026-09-17T10:00:00Z")
        val declinedAt = Instant.parse("2026-09-18T11:30:00Z")
        val account = PersonalAccount()
            .withConsentDecision(ConsentType.MarketingCommunications, ConsentStatus.Accepted, "marketing-v1", acceptedAt)
            .withConsentDecision(ConsentType.MarketingCommunications, ConsentStatus.Declined, "marketing-v2", declinedAt)
        val record = account.consents.single()

        assertEquals(2, record.decisions.size)
        assertEquals(ConsentStatus.Declined, record.currentDecision?.status)
        assertEquals(declinedAt, record.currentDecision?.recordedAt)
        assertEquals("marketing-v2", record.currentDecision?.textVersion)
    }

    @Test
    fun optionalConsentCanBeWithdrawnButRequiredConsentCannot() {
        val clock = Clock.fixed(Instant.parse("2026-09-20T12:00:00Z"), ZoneOffset.UTC)
        val withdrawn = PersonalAccount().withdrawOptionalConsent(
            ConsentType.ProductUpdatesAndNotifications,
            "notifications-v3",
            clock
        )

        assertEquals(ConsentStatus.Declined, withdrawn.consents.single().currentDecision?.status)
        assertTrue(ConsentType.ProductUpdatesAndNotifications.isOptional)
        assertThrows(IllegalArgumentException::class.java) {
            PersonalAccount().withdrawOptionalConsent(ConsentType.PrivacyPolicy, "privacy-v2", clock)
        }
    }

    @Test
    fun familyDataConsentBecomesRequiredWhenAFamilyMemberIsStored() {
        val emptyAccount = PersonalAccount()
        assertTrue(ConsentType.FamilyMemberDataProcessing !in emptyAccount.missingRequiredConsents())

        val accountWithFamily = emptyAccount.withFamilyMemberAdded(
            FamilyMember(
                id = "family-1",
                name = PersonName(firstName = "Alex"),
                relationship = Relationship(RelationshipType.Dependent)
            )
        )
        assertTrue(ConsentType.FamilyMemberDataProcessing in accountWithFamily.missingRequiredConsents())
    }

    @Test
    fun accountDeletionRequestStoresLifecycleTimestamp() {
        val instant = Instant.parse("2026-09-21T08:15:00Z")
        val account = PersonalAccount().requestAccountDeletion(Clock.fixed(instant, ZoneOffset.UTC))

        assertEquals(instant, account.deletionRequest?.requestedAt)
        assertEquals(AccountDeletionStatus.Requested, account.deletionRequest?.status)
    }
}
