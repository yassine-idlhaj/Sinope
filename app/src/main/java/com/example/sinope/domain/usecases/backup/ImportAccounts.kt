package com.example.sinope.domain.usecases.backup

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository

/** Saves the accounts the user selected in the preview. Returns how many were written. */
class ImportAccounts(
    private val accountRepository: IAccountRepository
) {
    suspend operator fun invoke(accounts: List<Account>): Int {
        if (accounts.isEmpty()) return 0
        accountRepository.insertAccounts(accounts)
        return accounts.size
    }
}
