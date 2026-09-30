package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.repository.account.IAccountRepository

/**
 * Wipes the vault. Irreversible: the secrets only ever existed on this device, so the only way
 * back is a backup file or re-scanning every QR code. Callers are expected to confirm first.
 */
class DeleteAllAccounts(
    private val repository: IAccountRepository
) {

    /** @return how many accounts were removed. */
    suspend operator fun invoke(): Int = repository.deleteAllAccounts()
}
