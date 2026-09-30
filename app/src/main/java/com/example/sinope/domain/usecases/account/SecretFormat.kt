package com.example.sinope.domain.usecases.account

import org.apache.commons.codec.binary.Base32

enum class ManualEntryValidation { Valid, MissingName, MissingSecret, InvalidSecret }

/** Sites print secrets in groups and in lower case; the stored form is always plain upper-case Base32. */
fun normalizeSecret(raw: String): String =
    raw.replace(" ", "").replace("-", "").uppercase()

/**
 * Rules for an account typed by hand. Catching a bad secret here is what stops an account that
 * silently produces wrong codes forever.
 */
class ValidateManualEntry {

    operator fun invoke(issuer: String, accountName: String, secret: String): ManualEntryValidation {
        val normalized = normalizeSecret(secret)
        return when {
            issuer.isBlank() && accountName.isBlank() -> ManualEntryValidation.MissingName
            normalized.isEmpty() -> ManualEntryValidation.MissingSecret
            !BASE32.matches(normalized) -> ManualEntryValidation.InvalidSecret
            Base32().decode(normalized).size < MIN_SECRET_BYTES -> ManualEntryValidation.InvalidSecret
            else -> ManualEntryValidation.Valid
        }
    }

    companion object {
        private val BASE32 = Regex("^[A-Z2-7]+=*$")
        private const val MIN_SECRET_BYTES = 10   // RFC 4226 minimum: 80 bits
    }
}
