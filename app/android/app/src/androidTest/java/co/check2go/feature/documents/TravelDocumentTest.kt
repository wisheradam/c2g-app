package co.check2go.feature.documents

import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TravelDocumentTest {
    private val now = Instant.parse("2026-01-01T00:00:00Z")

    @Test fun catalogContainsAllCategoriesAndRepresentativeTypes() {
        assertEquals(17, DocumentCatalog.categories.size)
        assertTrue(DocumentCategory.Identity.typeNames.contains("Diplomatic Passport"))
        assertTrue(DocumentCategory.Medical.typeNames.contains("Fit-to-Fly Certificate"))
        assertTrue(DocumentCategory.Pets.typeNames.contains("Pet Passport"))
        assertTrue(DocumentCategory.Other.typeNames.contains("Custom Document"))
    }

    @Test fun duplicateTypesRemainIndependentRecords() {
        val first = DocumentDraft.create(DocumentCategory.Identity, false).copy(name = "Israeli Passport").toDocument(now)
        val second = DocumentDraft.create(DocumentCategory.Identity, false).copy(name = "Polish Passport").toDocument(now)
        assertNotEquals(first.id, second.id)
        assertEquals(first.typeId, second.typeId)
    }

    @Test fun customDocumentUsesSelectedCategoryAndValidatesDates() {
        val draft = DocumentDraft.create(DocumentCategory.Customs, true).copy(
            name = "Camera import", customTypeName = "Film equipment declaration", expirationDate = "2027-05-01"
        )
        assertNull(draft.validationError())
        val document = draft.toDocument(now)
        assertEquals(DocumentCategory.Customs, document.category)
        assertEquals("Film equipment declaration", document.customTypeName)
    }

    @Test fun statusSearchAndExpirationSortWork() {
        val expired = document("Old Visa", DocumentCategory.Visas, LocalDate.of(2025, 1, 1), "Japan", "A123")
        val soon = document("Israeli Passport", DocumentCategory.Identity, LocalDate.of(2026, 3, 1), "Israel", "B456")
        val later = document("Insurance", DocumentCategory.Insurance, LocalDate.of(2030, 1, 1), "UK", "C789")
        assertEquals(DocumentStatus.Expired, expired.status(LocalDate.of(2026, 1, 1)))
        assertEquals(DocumentStatus.ExpiringSoon, soon.status(LocalDate.of(2026, 1, 1)))
        assertEquals(DocumentStatus.Valid, later.status(LocalDate.of(2026, 1, 1)))
        assertTrue(soon.matches("israel")); assertTrue(soon.matches("b456")); assertTrue(expired.matches("visas"))
        assertEquals(listOf(expired.id, soon.id, later.id), listOf(later, soon, expired).sortedByDocument(DocumentSort.Expiration).map { it.id })
    }

    private fun document(name: String, category: DocumentCategory, expiration: LocalDate, country: String, number: String) = TravelDocument(
        id = name, category = category, typeId = category.types.first().id, typeName = category.types.first().name,
        name = name, countryOfIssue = country, documentNumber = number, expirationDate = expiration, createdAt = now, updatedAt = now
    )
}
