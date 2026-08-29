package com.example.sinope.data.account.repository

import com.example.sinope.data.account.dao.AccountDao
import com.example.sinope.data.account.mapper.toDomain
import com.example.sinope.data.account.mapper.toEntity
import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl (
    private val accountDao: AccountDao
): IAccountRepository{

    override fun getAccounts(): Flow<List<Account>> =
        accountDao.getAccounts()
            .map { entities ->
                entities.map { it.toDomain() }
            }

    override fun getAccount(id: Long): Flow<Account?> =
        accountDao.getAccount(id)
            .map { entity -> entity?.toDomain() }

    override suspend fun insertAccount(account: Account) {
        accountDao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account.toEntity())
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account.toEntity())
    }

    override suspend fun updateFavorite(id: Long, favorite: Boolean) {
        accountDao.updateFavorite(id, favorite)
    }

    override suspend fun getAccountBySecret(secret: String): Account? {
        return  accountDao.getAccountBySecret(secret)?.toDomain()
    }
}