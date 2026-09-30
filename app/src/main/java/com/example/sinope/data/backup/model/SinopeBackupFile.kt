package com.example.sinope.data.backup.model

import kotlinx.serialization.Serializable

/**
 * The .sinope file on disk: a readable JSON header around the encrypted [BackupPayload].
 * Nothing here is secret except what's inside [ciphertext]; the rest is what an importer
 * needs to re-derive the key from the password and decrypt.
 */
@Serializable
data class SinopeBackupFile(
    val format: String = FORMAT,
    val version: Int = CURRENT_VERSION,
    val kdf: KdfParams,
    val cipher: CipherParams,
    val ciphertext: String,      // Base64, GCM tag included
) {
    companion object {
        const val FORMAT = "sinope-backup"
        const val CURRENT_VERSION = 1
    }
}

/** How the password was turned into a key. Stored so old files still open if defaults get stronger. */
@Serializable
data class KdfParams(
    val algorithm: String = ALGORITHM,
    val salt: String,            // Base64, random per export
    val memoryKiB: Int,
    val iterations: Int,
    val parallelism: Int,
) {
    companion object {
        const val ALGORITHM = "argon2id"
    }
}

@Serializable
data class CipherParams(
    val algorithm: String = ALGORITHM,
    val nonce: String,           // Base64, 12 random bytes
) {
    companion object {
        const val ALGORITHM = "AES-256-GCM"
    }
}
