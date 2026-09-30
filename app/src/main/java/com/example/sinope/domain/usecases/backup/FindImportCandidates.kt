package com.example.sinope.domain.usecases.backup

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.model.ImportCandidate
import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.flow.first

/**
 * Marks imported accounts that Sinope already has, using the same rule as Add Account:
 * issuer + account name, trimmed and case-insensitive.
 */
class FindImportCandidates(
    private val accountRepository: IAccountRepository
) {
    suspend operator fun invoke(imported: List<Account>): List<ImportCandidate> {
        // One read of the database instead of one query per imported account.
        val existing = accountRepository.getAccounts().first()
            .map { key(it.issuer, it.accountName) }
            .toSet()

        val seenInFile = mutableSetOf<String>()

        return imported.map { account ->
            val key = key(account.issuer, account.accountName)
            // Already in the database, or repeated earlier in the same file.
            val duplicate = key in existing || !seenInFile.add(key)
            ImportCandidate(account = account, isDuplicate = duplicate)
        }
    }

    private fun key(issuer: String, accountName: String) =
        "${issuer.trim().lowercase()}\u0000${accountName.trim().lowercase()}"
}
