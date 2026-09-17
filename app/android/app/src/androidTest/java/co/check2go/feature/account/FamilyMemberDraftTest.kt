package co.check2go.feature.account

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FamilyMemberDraftTest {
    @Test
    fun mapsSupportedFamilyFields() {
        val draft = FamilyMemberDraft(
            firstName = "Grace", lastName = "Hopper", dateOfBirth = "1906-12-09",
            gender = GenderOption.Female, citizenships = "us, GB", residenceCountryCode = "us",
            relationship = RelationshipType.Other, customRelationship = "Aunt"
        )
        assertNull(draft.validationError(LocalDate.of(2026, 1, 1)))
        val member = draft.toMember { "new-id" }
        assertEquals("new-id", member.id)
        assertEquals(setOf("US", "GB"), member.citizenshipCountryCodes)
        assertEquals("Aunt", member.relationship.customLabel)
        assertEquals(119, member.ageOn(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun otherRelationshipRequiresManualValue() {
        val error = FamilyMemberDraft(firstName = "A", lastName = "B").validationError(LocalDate.of(2026, 1, 1))
        assertEquals("Describe the relationship", error)
    }
}
