package co.check2go.feature.documents

import android.content.Context
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalDocumentStoreTest {
    @Test fun metadataAndAttachmentAreEncryptedAndRoundTrip() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalDocumentStore(context)
        val sourceBytes = "%PDF-private-document-content".toByteArray()
        val source = File(context.cacheDir, "source.pdf").apply { writeBytes(sourceBytes) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.documents", source)
        val attachment = store.importAttachment(uri)
        val document = TravelDocument(
            id = "doc-1", category = DocumentCategory.Identity, typeId = DocumentCategory.Identity.types.first().id,
            typeName = "Passport", name = "Secret Passport", attachments = listOf(attachment),
            createdAt = Instant.parse("2026-01-01T00:00:00Z"), updatedAt = Instant.parse("2026-01-01T00:00:00Z")
        )
        store.save(listOf(document))

        assertEquals(listOf(document), LocalDocumentStore(context).load())
        val rawMetadata = context.getSharedPreferences("travel_document_wallet", Context.MODE_PRIVATE)
            .getString("encrypted_documents_v1", "").orEmpty()
        assertFalse(rawMetadata.contains("Secret Passport"))
        val encryptedFile = File(context.filesDir, "document_wallet/${attachment.id}.bin")
        assertTrue(encryptedFile.exists())
        assertFalse(encryptedFile.readBytes().containsSubsequence(sourceBytes))
        assertEquals(sourceBytes.toList(), store.materializeAttachment(attachment).readBytes().toList())
        store.deleteDocument(document)
        store.save(emptyList())
        source.delete()
    }
}

private fun ByteArray.containsSubsequence(value: ByteArray): Boolean =
    indices.any { start -> start + value.size <= size && value.indices.all { this[start + it] == value[it] } }
