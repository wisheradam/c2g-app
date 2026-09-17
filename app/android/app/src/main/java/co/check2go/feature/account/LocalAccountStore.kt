package co.check2go.feature.account

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.time.Instant
import java.time.LocalDate
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.json.JSONArray
import org.json.JSONObject

/** Device-only encrypted persistence for the personal account. No network API is used. */
class LocalAccountStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun load(): PersonalAccount {
        val encrypted = preferences.getString(ACCOUNT_KEY, null) ?: return PersonalAccount()
        return runCatching {
            val envelope = JSONObject(encrypted)
            val iv = Base64.decode(envelope.getString("iv"), Base64.NO_WRAP)
            val ciphertext = Base64.decode(envelope.getString("data"), Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
            PersonalAccountJson.decode(cipher.doFinal(ciphertext).toString(Charsets.UTF_8))
        }.getOrElse { PersonalAccount() }
    }

    fun save(account: PersonalAccount) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val ciphertext = cipher.doFinal(PersonalAccountJson.encode(account).toByteArray(Charsets.UTF_8))
        val envelope = JSONObject()
            .put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .put("data", Base64.encodeToString(ciphertext, Base64.NO_WRAP))
        check(preferences.edit().putString(ACCOUNT_KEY, envelope.toString()).commit()) {
            "Could not persist personal account"
        }
    }

    fun clear() {
        check(preferences.edit().remove(ACCOUNT_KEY).commit()) { "Could not clear personal account" }
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").run {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
            generateKey()
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "personal_account_device_only"
        const val ACCOUNT_KEY = "encrypted_account_v1"
        const val KEY_ALIAS = "check2go_personal_account_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}

internal object PersonalAccountJson {
    fun encode(account: PersonalAccount): String = JSONObject().apply {
        put("schemaVersion", account.schemaVersion)
        put("name", nameJson(account.name))
        put("gender", genderJson(account.gender))
        putNullable("dateOfBirth", account.dateOfBirth?.toString())
        putNullable("address", account.address?.let(::addressJson))
        put("citizenships", strings(account.citizenshipCountryCodes))
        putNullable("residence", account.residence?.let(::residenceJson))
        put("residencePermits", array(account.residencePermits, ::permitJson))
        put("entryRestrictions", array(account.entryRestrictions, ::restrictionJson))
        put("familyMembers", array(account.familyMembers, ::familyJson))
        put("interests", array(account.interests, ::interestJson))
        put("consents", array(account.consents, ::consentJson))
        putNullable("deletionRequest", account.deletionRequest?.let(::deletionJson))
    }.toString()

    fun decode(value: String): PersonalAccount {
        val json = JSONObject(value)
        return PersonalAccount(
            schemaVersion = json.optInt("schemaVersion", 1),
            name = json.getJSONObject("name").toName(),
            gender = json.getJSONObject("gender").toGender(),
            dateOfBirth = json.optStringOrNull("dateOfBirth")?.let(LocalDate::parse),
            address = json.optObject("address")?.toAddress(),
            citizenshipCountryCodes = json.optArray("citizenships").strings().toSet(),
            residence = json.optObject("residence")?.toResidence(),
            residencePermits = json.optArray("residencePermits").objects { it.toPermit() },
            entryRestrictions = json.optArray("entryRestrictions").objects { it.toRestriction() },
            familyMembers = json.optArray("familyMembers").objects { it.toFamilyMember() },
            interests = json.optArray("interests").objects { it.toInterest() },
            consents = json.optArray("consents").objects { it.toConsent() },
            deletionRequest = json.optObject("deletionRequest")?.toDeletionRequest()
        )
    }

    private fun nameJson(value: PersonName) = JSONObject().apply {
        put("first", value.firstName); put("last", value.lastName); putNullable("middle", value.middleName)
    }
    private fun JSONObject.toName() = PersonName(getString("first"), getString("last"), optStringOrNull("middle"))

    private fun genderJson(value: Gender) = JSONObject().apply {
        put("option", value.option.name); putNullable("custom", value.customLabel)
    }
    private fun JSONObject.toGender() = Gender(enumValueOf(getString("option")), optStringOrNull("custom"))

    private fun addressJson(value: PostalAddress) = JSONObject().apply {
        put("line1", value.addressLine1); putNullable("line2", value.addressLine2); put("city", value.city)
        putNullable("region", value.region); putNullable("postalCode", value.postalCode); put("country", value.countryCode)
    }
    private fun JSONObject.toAddress() = PostalAddress(
        getString("line1"), optStringOrNull("line2"), getString("city"), optStringOrNull("region"),
        optStringOrNull("postalCode"), getString("country")
    )

    private fun residenceJson(value: Residence) = JSONObject().apply {
        put("country", value.countryCode); put("place", value.place); put("status", value.status.name)
    }
    private fun JSONObject.toResidence() = Residence(getString("country"), getString("place"), enumValueOf(getString("status")))

    private fun permitJson(value: ResidencePermit) = JSONObject().apply {
        put("id", value.id); put("country", value.countryCode); put("status", value.status.name)
        putNullable("type", value.permitType); putNullable("from", value.validFrom?.toString()); putNullable("until", value.validUntil?.toString())
    }
    private fun JSONObject.toPermit() = ResidencePermit(
        getString("id"), getString("country"), enumValueOf(getString("status")), optStringOrNull("type"),
        optStringOrNull("from")?.let(LocalDate::parse), optStringOrNull("until")?.let(LocalDate::parse)
    )

    private fun restrictionJson(value: EntryRestriction) = JSONObject().apply {
        put("id", value.id); put("country", value.countryCode); put("type", value.type.name); put("details", value.details)
        putNullable("from", value.effectiveFrom?.toString()); putNullable("until", value.effectiveUntil?.toString())
    }
    private fun JSONObject.toRestriction() = EntryRestriction(
        getString("id"), getString("country"), enumValueOf(getString("type")), getString("details"),
        optStringOrNull("from")?.let(LocalDate::parse), optStringOrNull("until")?.let(LocalDate::parse)
    )

    private fun familyJson(value: FamilyMember) = JSONObject().apply {
        put("id", value.id); put("name", nameJson(value.name)); putNullable("dateOfBirth", value.dateOfBirth?.toString())
        put("gender", genderJson(value.gender)); put("citizenships", strings(value.citizenshipCountryCodes))
        putNullable("residenceCountry", value.residenceCountryCode)
        put("relationship", JSONObject().put("type", value.relationship.type.name).putNullable("custom", value.relationship.customLabel))
    }
    private fun JSONObject.toFamilyMember() = FamilyMember(
        getString("id"), getJSONObject("name").toName(), optStringOrNull("dateOfBirth")?.let(LocalDate::parse),
        getJSONObject("gender").toGender(), optArray("citizenships").strings().toSet(), optStringOrNull("residenceCountry"),
        getJSONObject("relationship").let { Relationship(enumValueOf(it.getString("type")), it.optStringOrNull("custom")) }
    )

    private fun interestJson(value: Interest) = JSONObject().put("id", value.id).put("label", value.label)
    private fun JSONObject.toInterest() = Interest(getString("id"), getString("label"))

    private fun consentJson(value: ConsentRecord) = JSONObject().apply {
        put("type", value.type.name)
        put("decisions", array(value.decisions) { decision ->
            JSONObject().put("status", decision.status.name).put("recordedAt", decision.recordedAt.toString()).put("textVersion", decision.textVersion)
        })
    }
    private fun JSONObject.toConsent() = ConsentRecord(
        enumValueOf(getString("type")),
        optArray("decisions").objects {
            ConsentDecision(enumValueOf(it.getString("status")), Instant.parse(it.getString("recordedAt")), it.getString("textVersion"))
        }
    )

    private fun deletionJson(value: AccountDeletionRequest) = JSONObject().apply {
        put("requestedAt", value.requestedAt.toString()); put("status", value.status.name); putNullable("completedAt", value.completedAt?.toString())
    }
    private fun JSONObject.toDeletionRequest() = AccountDeletionRequest(
        Instant.parse(getString("requestedAt")), enumValueOf(getString("status")), optStringOrNull("completedAt")?.let(Instant::parse)
    )

    private fun strings(values: Collection<String>) = JSONArray().apply { values.forEach(::put) }
    private fun <T> array(values: Collection<T>, encode: (T) -> JSONObject) = JSONArray().apply { values.forEach { put(encode(it)) } }
    private fun JSONObject.putNullable(key: String, value: Any?): JSONObject = put(key, value ?: JSONObject.NULL)
    private fun JSONObject.optStringOrNull(key: String): String? = if (!has(key) || isNull(key)) null else getString(key)
    private fun JSONObject.optObject(key: String): JSONObject? = if (!has(key) || isNull(key)) null else getJSONObject(key)
    private fun JSONObject.optArray(key: String): JSONArray = optJSONArray(key) ?: JSONArray()
    private fun JSONArray.strings(): List<String> = List(length()) { getString(it) }
    private fun <T> JSONArray.objects(transform: (JSONObject) -> T): List<T> = List(length()) { transform(getJSONObject(it)) }
}
