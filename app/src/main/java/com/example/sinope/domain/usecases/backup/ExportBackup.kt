package com.example.sinope.domain.usecases.backup

import com.example.sinope.domain.repository.account.IAccountRepository
import com.example.sinope.domain.repository.backup.IBackupRepository
import kotlinx.coroutines.flow.first

/**
 * Reads the accounts (already decrypted by the account repository) and hands them to the backup
 * repository for password encryption. Returns how many accounts were exported.
 */
class ExportBackup(
    private val accountRepository: IAccountRepository,
    private val backupRepository: IBackupRepository,
) {
    suspend operator fun invoke(password: CharArray, destinationUri: String): Int {
        val accounts = accountRepository.getAccounts().first()
        backupRepository.exportBackup(accounts, password, destinationUri)
        return accounts.size
    }
}
