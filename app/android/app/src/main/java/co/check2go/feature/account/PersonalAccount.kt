package co.check2go.feature.account

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.Period

/**
 * Initial, backend-agnostic personal-account contract. It deliberately keeps country identifiers
 * as ISO-like strings until the shared API country catalogue is approved.
 */
data class PersonalAccount(
    val schemaVersion: Int = 1,
    val name: PersonName = PersonName(),
    val gender: Gender = Gender(),
    val dateOfBirth: LocalDate? = null,
    val address: PostalAddress? = null,
    val citizenshipCountryCodes: Set<String> = emptySet(),
    val residence: Residence? = null,
    val residencePermits: List<ResidencePermit> = emptyList(),
    val entryRestrictions: List<EntryRestriction> = emptyList(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val interests: List<Interest> = emptyList(),
    val consents: List<ConsentRecord> = emptyList(),
    val deletionRequest: AccountDeletionRequest? = null
) {
    fun ageOn(date: LocalDate): Int? = dateOfBirth?.let { birthDate ->
        if (birthDate.isAfter(date)) null else Period.between(birthDate, date).years
    }

    fun withInterestAdded(interest: Interest): PersonalAccount =
        if (interests.any { it.id == interest.id }) this else copy(interests = interests + interest)

    fun withInterestRemoved(interestId: String): PersonalAccount =
        copy(interests = interests.filterNot { it.id == interestId })

    fun withFamilyMemberAdded(member: FamilyMember): PersonalAccount =
        if (familyMembers.any { it.id == member.id }) this else copy(familyMembers = familyMembers + member)

    fun withFamilyMemberRemoved(memberId: String): PersonalAccount =
        copy(familyMembers = familyMembers.filterNot { it.id == memberId })

    fun withConsentDecision(
        type: ConsentType,
        status: ConsentStatus,
        textVersion: String,
        recordedAt: Instant
    ): PersonalAccount {
        val existing = consents.firstOrNull { it.type == type } ?: ConsentRecord(type)
        val updated = existing.copy(
            decisions = existing.decisions + ConsentDecision(status, recordedAt, textVersion)
        )
        return copy(consents = consents.filterNot { it.type == type } + updated)
    }

    fun missingRequiredConsents(): Set<ConsentType> {
        val required = ConsentType.entries.filterTo(mutableSetOf()) {
            !it.isOptional && (it != ConsentType.FamilyMemberDataProcessing || familyMembers.isNotEmpty())
        }
        val accepted = consents.filter { it.currentDecision?.status == ConsentStatus.Accepted }.map { it.type }.toSet()
        return required - accepted
    }

    /** Required account consents cannot be withdrawn through preference controls. */
    fun withdrawOptionalConsent(type: ConsentType, textVersion: String, clock: Clock): PersonalAccount {
        require(type.isOptional) { "$type is required and cannot be withdrawn as an optional consent" }
        return withConsentDecision(type, ConsentStatus.Declined, textVersion, clock.instant())
    }

    fun requestAccountDeletion(clock: Clock): PersonalAccount = copy(
        deletionRequest = AccountDeletionRequest(requestedAt = clock.instant())
    )
}

data class PersonName(
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String? = null
)

enum class GenderOption { Unspecified, Female, Male, NonBinary, Other, PreferNotToSay }

data class Gender(
    val option: GenderOption = GenderOption.Unspecified,
    val customLabel: String? = null
) {
    init {
        require(option == GenderOption.Other || customLabel == null) {
            "A custom gender label is only valid for Other"
        }
    }
}

data class PostalAddress(
    val addressLine1: String = "",
    val addressLine2: String? = null,
    val city: String = "",
    val region: String? = null,
    val postalCode: String? = null,
    val countryCode: String = ""
)

data class Residence(
    val countryCode: String,
    val place: String,
    val status: ResidencyStatus = ResidencyStatus.Unknown
)

enum class ResidencyStatus { Citizen, PermanentResident, TemporaryResident, VisaHolder, Unknown, Other }

data class ResidencePermit(
    val id: String,
    val countryCode: String,
    val status: ResidencyStatus,
    val permitType: String? = null,
    val validFrom: LocalDate? = null,
    val validUntil: LocalDate? = null
)

enum class EntryRestrictionType { EntryRestriction, TravelBan }

data class EntryRestriction(
    val id: String,
    val countryCode: String,
    val type: EntryRestrictionType,
    val details: String,
    val effectiveFrom: LocalDate? = null,
    val effectiveUntil: LocalDate? = null
)

enum class RelationshipType {
    Spouse,
    Partner,
    Child,
    Parent,
    Sibling,
    Grandparent,
    Grandchild,
    LegalGuardian,
    Dependent,
    Other
}

data class Relationship(
    val type: RelationshipType,
    val customLabel: String? = null
) {
    init {
        require(type == RelationshipType.Other || customLabel == null) {
            "A custom relationship is only valid for Other"
        }
        require(type != RelationshipType.Other || !customLabel.isNullOrBlank()) {
            "Other relationship requires a custom value"
        }
    }
}

data class FamilyMember(
    val id: String,
    val name: PersonName,
    val dateOfBirth: LocalDate? = null,
    val gender: Gender = Gender(),
    val citizenshipCountryCodes: Set<String> = emptySet(),
    val residenceCountryCode: String? = null,
    val relationship: Relationship
) {
    fun ageOn(date: LocalDate): Int? = dateOfBirth?.let { birthDate ->
        if (birthDate.isAfter(date)) null else Period.between(birthDate, date).years
    }
}

data class Interest(val id: String, val label: String) {
    init {
        require(label.isNotBlank()) { "Interest label cannot be blank" }
    }
}

enum class ConsentStatus { Accepted, Declined }

enum class ConsentType(val isOptional: Boolean) {
    PersonalInformationUse(false),
    PrivacyPolicy(false),
    TermsOfService(false),
    PersonalDataProcessing(false),
    PersonalizedRecommendations(false),
    FamilyMemberDataProcessing(false),
    MarketingCommunications(true),
    ProductUpdatesAndNotifications(true)
}

data class ConsentDecision(
    val status: ConsentStatus,
    val recordedAt: Instant,
    val textVersion: String
) {
    init {
        require(textVersion.isNotBlank()) { "Consent text version cannot be blank" }
    }
}

data class ConsentRecord(
    val type: ConsentType,
    val decisions: List<ConsentDecision> = emptyList()
) {
    val currentDecision: ConsentDecision? get() = decisions.maxByOrNull { it.recordedAt }
}

enum class AccountDeletionStatus { Requested, Processing, Completed, Cancelled }

data class AccountDeletionRequest(
    val requestedAt: Instant,
    val status: AccountDeletionStatus = AccountDeletionStatus.Requested,
    val completedAt: Instant? = null
)
