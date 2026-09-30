package com.example.sinope.testing

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory stand-in for the Room-backed account repository.
 *
 * Hand-written to match [FakeLocalUserPreferences]: because it really stores the accounts, a test
 * can assert on behaviour — "the vault is empty afterwards, and anyone watching it was told" —
 * rather than on interaction bookkeeping.
 */
class FakeAccountRepository(initial: List<Account> = emptyList()) : IAccountRepository {

    private val accounts = MutableStateFlow(initial)

    /** Current contents, for assertions that don't need the flow. */
    val current: List<Account> get() = accounts.value

    override fun getAccounts(): Flow<List<Account>> = accounts

    override fun getAccount(id: Long): Flow<Account?> =
        accounts.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun insertAccount(account: Account) {
        accounts.value += account
    }

    override suspend fun insertAccounts(accounts: List<Account>) {
        this.accounts.value += accounts
    }

    override suspend fun updateAccount(account: Account) {
        accounts.value = accounts.value.map { if (it.id == account.id) account else it }
    }

    override suspend fun deleteAccount(account: Account) {
        accounts.value = accounts.value.filterNot { it.id == account.id }
    }

    override suspend fun deleteAllAccounts(): Int {
        val removed = accounts.value.size
        accounts.value = emptyList()
        return removed
    }

    override suspend fun updateFavorite(id: Long, favorite: Boolean) {
        accounts.value = accounts.value.map {
            if (it.id == id) it.copy(favorite = favorite) else it
        }
    }

    override suspend fun accountExists(issuer: String, accountName: String): Boolean =
        accounts.value.any {
            it.issuer.equals(issuer, ignoreCase = true) &&
                    it.accountName.equals(accountName, ignoreCase = true)
        }
}
