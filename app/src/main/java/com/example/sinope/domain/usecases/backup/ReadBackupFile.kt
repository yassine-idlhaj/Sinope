package com.example.sinope.domain.usecases.backup

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.backup.IBackupRepository

/**
 * Opens a .sinope file with the user's password and returns what it holds.
 * Read-only: the database is untouched until the user confirms the import.
 */
class ReadBackupFile(
    private val backupRepository: IBackupRepository
) {
    suspend operator fun invoke(sourceUri: String, password: CharArray): List<Account> =
        backupRepository.readBackup(sourceUri, password)
}
