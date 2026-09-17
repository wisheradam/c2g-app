package co.check2go.feature.documents

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.json.JSONArray
import org.json.JSONObject

class LocalDocumentStore(private val context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val attachmentsDirectory = File(context.filesDir, "document_wallet").apply { mkdirs() }

    fun load(): List<TravelDocument> {
        val envelope = preferences.getString(DOCUMENTS_KEY, null) ?: return emptyList()
        return runCatching {
            val json = JSONObject(envelope)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, Base64.decode(json.getString("iv"), Base64.NO_WRAP)))
            DocumentJson.decode(cipher.doFinal(Base64.decode(json.getString("data"), Base64.NO_WRAP)).toString(Charsets.UTF_8))
        }.getOrElse { emptyList() }
    }

    fun save(documents: List<TravelDocument>) {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, secretKey()) }
        val encrypted = cipher.doFinal(DocumentJson.encode(documents).toByteArray())
        val envelope = JSONObject()
            .put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .put("data", Base64.encodeToString(encrypted, Base64.NO_WRAP))
        check(preferences.edit().putString(DOCUMENTS_KEY, envelope.toString()).commit()) { "Could not persist documents" }
    }

    fun importAttachment(uri: Uri, role: AttachmentRole = AttachmentRole.General): DocumentAttachment {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri) ?: "application/octet-stream"
        require(mimeType in ALLOWED_MIME_TYPES) { "Unsupported file type" }
        var displayName = "attachment"
        var size = 0L
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                displayName = cursor.getString(0) ?: displayName
                size = cursor.getLong(1).coerceAtLeast(0)
            }
        }
        val id = UUID.randomUUID().toString()
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, secretKey()) }
        FileOutputStream(attachmentFile(id)).use { output ->
            output.write(cipher.iv.size)
            output.write(cipher.iv)
            CipherOutputStream(output, cipher).use { encryptedOutput ->
                resolver.openInputStream(uri).use { input -> requireNotNull(input) { "Cannot open attachment" }.copyTo(encryptedOutput) }
            }
        }
        return DocumentAttachment(id, displayName, mimeType, size, Instant.now(), role)
    }

    fun materializeAttachment(attachment: DocumentAttachment): File {
        val extension = when (attachment.mimeType) {
            "application/pdf" -> ".pdf"
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> ""
        }
        val target = File(context.cacheDir, "document_${attachment.id}$extension")
        FileInputStream(attachmentFile(attachment.id)).use { input ->
            val ivLength = input.read()
            require(ivLength in 12..32) { "Invalid encrypted attachment" }
            val iv = ByteArray(ivLength)
            require(input.read(iv) == ivLength) { "Invalid encrypted attachment" }
            val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv)) }
            CipherInputStream(input, cipher).use { decrypted -> FileOutputStream(target).use(decrypted::copyTo) }
        }
        return target
    }

    fun deleteAttachment(id: String) {
        attachmentFile(id).delete()
        context.cacheDir.listFiles()?.filter { it.name.startsWith("document_$id") }?.forEach(File::delete)
    }

    fun deleteDocument(document: TravelDocument) = document.attachments.forEach { deleteAttachment(it.id) }
    private fun attachmentFile(id: String) = File(attachmentsDirectory, "$id.bin")

    private fun secretKey(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").run {
            init(KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true).build())
            generateKey()
        }
    }

    companion object {
        val ALLOWED_MIME_TYPES = setOf("application/pdf", "image/jpeg", "image/png")
        private const val PREFERENCES_NAME = "travel_document_wallet"
        private const val DOCUMENTS_KEY = "encrypted_documents_v1"
        private const val KEY_ALIAS = "check2go_document_wallet_v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}

internal object DocumentJson {
    fun encode(documents: List<TravelDocument>) = JSONArray().apply { documents.forEach { put(encodeDocument(it)) } }.toString()
    fun decode(value: String): List<TravelDocument> = JSONArray(value).objects { decodeDocument(it) }

    private fun encodeDocument(d: TravelDocument) = JSONObject().apply {
        put("id", d.id); put("category", d.category.name); put("typeId", d.typeId); put("typeName", d.typeName)
        nullable("customTypeName", d.customTypeName); put("name", d.name); nullable("description", d.description)
        nullable("countryOfIssue", d.countryOfIssue); nullable("documentNumber", d.documentNumber)
        nullable("issueDate", d.issueDate?.toString()); nullable("expirationDate", d.expirationDate?.toString())
        nullable("validFrom", d.validFrom?.toString()); nullable("validUntil", d.validUntil?.toString())
        nullable("issuingAuthority", d.issuingAuthority); nullable("citizenship", d.citizenship)
        nullable("destinationCountry", d.destinationCountry); nullable("notes", d.notes)
        put("attachments", JSONArray().apply { d.attachments.forEach { put(encodeAttachment(it)) } })
        put("primary", d.isPrimary); put("recommendations", d.useForTravelRecommendations)
        nullable("ownerReference", d.ownerReference); nullable("tripReference", d.tripReference)
        put("source", d.source.name); put("verification", d.verificationStatus.name)
        put("notifications", encodeMap(d.notificationSettings)); put("countryMetadata", encodeMap(d.countryMetadata)); put("extractedData", encodeMap(d.extractedData))
        put("createdAt", d.createdAt.toString()); put("updatedAt", d.updatedAt.toString()); put("version", d.version)
    }

    private fun decodeDocument(j: JSONObject) = TravelDocument(
        id = j.getString("id"), category = enumValueOf(j.getString("category")), typeId = j.getString("typeId"),
        typeName = j.getString("typeName"), customTypeName = j.stringOrNull("customTypeName"), name = j.getString("name"),
        description = j.stringOrNull("description"), countryOfIssue = j.stringOrNull("countryOfIssue"),
        documentNumber = j.stringOrNull("documentNumber"), issueDate = j.dateOrNull("issueDate"),
        expirationDate = j.dateOrNull("expirationDate"), validFrom = j.dateOrNull("validFrom"), validUntil = j.dateOrNull("validUntil"),
        issuingAuthority = j.stringOrNull("issuingAuthority"), citizenship = j.stringOrNull("citizenship"),
        destinationCountry = j.stringOrNull("destinationCountry"), notes = j.stringOrNull("notes"),
        attachments = j.optJSONArray("attachments")?.objects(::decodeAttachment).orEmpty(),
        isPrimary = j.optBoolean("primary"), useForTravelRecommendations = j.optBoolean("recommendations"),
        ownerReference = j.stringOrNull("ownerReference"), tripReference = j.stringOrNull("tripReference"),
        source = enumValueOf(j.optString("source", DocumentSource.Manual.name)),
        verificationStatus = enumValueOf(j.optString("verification", VerificationStatus.Unverified.name)),
        notificationSettings = decodeMap(j.optJSONObject("notifications")), countryMetadata = decodeMap(j.optJSONObject("countryMetadata")),
        extractedData = decodeMap(j.optJSONObject("extractedData")), createdAt = Instant.parse(j.getString("createdAt")),
        updatedAt = Instant.parse(j.getString("updatedAt")), version = j.optInt("version", 1)
    )

    private fun encodeAttachment(a: DocumentAttachment) = JSONObject().put("id", a.id).put("name", a.displayName)
        .put("mime", a.mimeType).put("size", a.sizeBytes).put("addedAt", a.addedAt.toString()).put("role", a.role.name)
    private fun decodeAttachment(j: JSONObject) = DocumentAttachment(j.getString("id"), j.getString("name"), j.getString("mime"), j.getLong("size"), Instant.parse(j.getString("addedAt")), enumValueOf(j.getString("role")))
    private fun encodeMap(map: Map<String, String>) = JSONObject().apply { map.forEach(::put) }
    private fun decodeMap(json: JSONObject?): Map<String, String> = json?.keys()?.asSequence()?.associateWith(json::getString).orEmpty()
    private fun JSONObject.nullable(key: String, value: Any?) = put(key, value ?: JSONObject.NULL)
    private fun JSONObject.stringOrNull(key: String) = if (!has(key) || isNull(key)) null else getString(key)
    private fun JSONObject.dateOrNull(key: String) = stringOrNull(key)?.let(LocalDate::parse)
    private fun <T> JSONArray.objects(transform: (JSONObject) -> T) = List(length()) { transform(getJSONObject(it)) }
}
