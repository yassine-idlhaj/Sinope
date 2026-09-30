package com.example.sinope.domain.repository.backup

import com.example.sinope.domain.model.Account

interface IBackupRepository {

    /**
     * Encrypts [accounts] with [password] and writes a .sinope file to [destinationUri].
     * The URI is a plain String so the domain stays free of Android types.
     */
    suspend fun exportBackup(accounts: List<Account>, password: CharArray, destinationUri: String)

    /** Decrypts a .sinope file and returns its accounts. Nothing is saved yet. */
    suspend fun readBackup(sourceUri: String, password: CharArray): List<Account>
}
