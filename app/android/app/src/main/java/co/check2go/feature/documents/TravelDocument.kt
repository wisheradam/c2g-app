package co.check2go.feature.documents

import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DocumentAttachment(
    val id: String,
    val displayName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val addedAt: Instant,
    val role: AttachmentRole = AttachmentRole.General
)

enum class AttachmentRole { General, FrontSide, BackSide }
enum class DocumentSource { Manual, Imported, Scanned, Classified }
enum class VerificationStatus { Unverified, Pending, Verified, Rejected }

data class TravelDocument(
    val id: String,
    val category: DocumentCategory,
    val typeId: String,
    val typeName: String,
    val customTypeName: String? = null,
    val name: String,
    val description: String? = null,
    val countryOfIssue: String? = null,
    val documentNumber: String? = null,
    val issueDate: LocalDate? = null,
    val expirationDate: LocalDate? = null,
    val validFrom: LocalDate? = null,
    val validUntil: LocalDate? = null,
    val issuingAuthority: String? = null,
    val citizenship: String? = null,
    val destinationCountry: String? = null,
    val notes: String? = null,
    val attachments: List<DocumentAttachment> = emptyList(),
    val isPrimary: Boolean = false,
    val useForTravelRecommendations: Boolean = false,
    val ownerReference: String? = null,
    val tripReference: String? = null,
    val source: DocumentSource = DocumentSource.Manual,
    val verificationStatus: VerificationStatus = VerificationStatus.Unverified,
    val notificationSettings: Map<String, String> = emptyMap(),
    val countryMetadata: Map<String, String> = emptyMap(),
    val extractedData: Map<String, String> = emptyMap(),
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Int = 1
) {
    fun status(on: LocalDate = LocalDate.now(), warningDays: Long = DEFAULT_WARNING_DAYS): DocumentStatus {
        val start = validFrom ?: issueDate
        val end = validUntil ?: expirationDate
        if (start != null && start.isAfter(on)) return DocumentStatus.Future
        if (end == null) return DocumentStatus.MissingExpiration
        if (end.isBefore(on)) return DocumentStatus.Expired
        return if (ChronoUnit.DAYS.between(on, end) <= warningDays) DocumentStatus.ExpiringSoon else DocumentStatus.Valid
    }

    fun matches(query: String, localizedCategory: String = category.displayName, localizedType: String = typeName): Boolean {
        val normalized = query.trim().lowercase()
        if (normalized.isEmpty()) return true
        return listOfNotNull(name, typeName, localizedType, customTypeName, category.displayName, localizedCategory, countryOfIssue, documentNumber,
            citizenship, destinationCountry, notes, description).any { it.lowercase().contains(normalized) }
    }

    companion object { const val DEFAULT_WARNING_DAYS = 180L }
}

enum class DocumentStatus(val label: String) {
    Valid("Valid"), ExpiringSoon("Expiring Soon"), Expired("Expired"), Future("Future / Not Yet Valid"),
    MissingExpiration("Missing Expiration Date")
}

enum class DocumentSort(val label: String) { Expiration("Expiration date"), Name("Name"), DateAdded("Date added"), Country("Country") }

fun List<TravelDocument>.sortedByDocument(option: DocumentSort): List<TravelDocument> = when (option) {
    DocumentSort.Expiration -> sortedWith(compareBy<TravelDocument> { it.validUntil ?: it.expirationDate ?: LocalDate.MAX }.thenBy { it.name.lowercase() })
    DocumentSort.Name -> sortedBy { it.name.lowercase() }
    DocumentSort.DateAdded -> sortedByDescending { it.createdAt }
    DocumentSort.Country -> sortedWith(compareBy<TravelDocument> { it.countryOfIssue.orEmpty().lowercase() }.thenBy { it.name.lowercase() })
}
