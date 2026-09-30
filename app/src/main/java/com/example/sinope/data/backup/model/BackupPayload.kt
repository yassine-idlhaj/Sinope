package com.example.sinope.data.backup.model

import kotlinx.serialization.Serializable

/**
 * Plaintext content of a .sinope backup, before password encryption.
 * This is a file format (data layer), kept separate from the domain [com.example.sinope.domain.model.Account]
 * so old backups stay readable when the domain model changes.
 */
@Serializable
data class BackupPayload(
    val version: Int,
    val createdAt: Long,
    val accounts: List<BackupAccount>,
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}

/** One account inside a backup. No id: Room ids are device-local, import assigns new ones. */
@Serializable
data class BackupAccount(
    val issuer: String,
    val accountName: String,
    val secret: String,
    val algorithm: String,
    val digits: Int,
    val period: Int,
    val emoji: String,
    val color: Long,
    val favorite: Boolean,
)
