package com.example.sinope.domain.usecases.backup

import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.flow.first

/** Checked before asking for a password, so the user never creates an empty backup file. */
class HasAccountsToExport(
    private val accountRepository: IAccountRepository
) {
    suspend operator fun invoke(): Boolean =
        accountRepository.getAccounts().first().isNotEmpty()
}
