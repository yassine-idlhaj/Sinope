package com.example.sinope.data.account.repository

import android.util.Log
import com.example.sinope.data.account.dao.AccountDao
import com.example.sinope.data.account.entity.AccountEntity
import com.example.sinope.data.account.mapper.toDomain
import com.example.sinope.data.account.mapper.toEntity
import com.example.sinope.data.security.CryptoManager
import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val cryptoManager: CryptoManager
) : IAccountRepository {

    companion object {
        private const val TAG = "AccountRepository"
        private val BASE32_REGEX = Regex("^[A-Z2-7]+=*$")
    }

    private val legacyMigrationMutex = Mutex()

    @Volatile
    private var legacyMigrationDone = false

    override fun getAccounts(): Flow<List<Account>> =
        accountDao.getAccounts()
            .onStart { encryptLegacySecretsOnce() }
            .map { entities ->
                entities.mapNotNull { it.toDecryptedDomain() }
            }
            .flowOn(Dispatchers.IO)

    override fun getAccount(id: Long): Flow<Account?> =
        accountDao.getAccount(id)
            .onStart { encryptLegacySecretsOnce() }
            .map { entity -> entity?.toDecryptedDomain() }
            .flowOn(Dispatchers.IO)

    override suspend fun insertAccount(account: Account) {
        val encryptedSecret = withContext(Dispatchers.IO) {
            cryptoManager.encrypt(account.secret)
        }
        accountDao.insertAccount(
            account.copy(secret = encryptedSecret).toEntity()
        )
    }

    override suspend fun updateAccount(account: Account) {
        val encryptedSecret = withContext(Dispatchers.IO) {
            cryptoManager.encrypt(account.secret)
        }
        accountDao.updateAccount(
            account.copy(secret = encryptedSecret).toEntity()
        )
    }

    override suspend fun insertAccounts(accounts: List<Account>) {
        val entities = withContext(Dispatchers.IO) {
            accounts.map { account ->
                account.copy(secret = cryptoManager.encrypt(account.secret)).toEntity()
            }
        }
        accountDao.insertAccounts(entities)
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account.toEntity())
    }

    override suspend fun deleteAllAccounts(): Int = accountDao.deleteAllAccounts()

    override suspend fun updateFavorite(id: Long, favorite: Boolean) {
        accountDao.updateFavorite(id, favorite)
    }

    override suspend fun accountExists(issuer: String, accountName: String): Boolean {
        return accountDao.accountExists(issuer, accountName)
    }

    /**
     * Decrypts the stored secret. Returns null for a row that cannot be decrypted,
     * so one bad row hides itself instead of crashing the whole list.
     */
    private fun AccountEntity.toDecryptedDomain(): Account? {
        val plainSecret = cryptoManager.decryptOrNull(secret)

        if (plainSecret == null) {
            // Never log the secret itself, only the row id.
            Log.w(TAG, "Account id=$id has an unreadable secret, hiding it")
            return null
        }

        return toDomain().copy(secret = plainSecret)
    }

    /**
     * One-time conversion of rows written before encryption existed.
     * Runs once per process, before the first read. Safe to re-run: rows that
     * already decrypt are skipped.
     */
    private suspend fun encryptLegacySecretsOnce() {
        if (legacyMigrationDone) return

        legacyMigrationMutex.withLock {
            if (legacyMigrationDone) return

            accountDao.getAccountsOnce().forEach { entity ->
                val alreadyEncrypted = cryptoManager.decryptOrNull(entity.secret) != null

                if (!alreadyEncrypted && looksLikePlainBase32(entity.secret)) {
                    accountDao.updateSecret(
                        id = entity.id,
                        secret = cryptoManager.encrypt(entity.secret)
                    )
                    Log.i(TAG, "Encrypted legacy secret for account id=${entity.id}")
                }
            }

            legacyMigrationDone = true
        }
    }

    private fun looksLikePlainBase32(value: String): Boolean =
        BASE32_REGEX.matches(value.uppercase().replace(" ", ""))
}
