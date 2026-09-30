package com.example.sinope.domain.repository.account

import com.example.sinope.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface IAccountRepository {

    fun getAccounts(): Flow<List<Account>>

    fun getAccount(id: Long): Flow<Account?>

    suspend fun insertAccount(account: Account)

    suspend fun updateAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    /** Removes every account. Returns how many were removed. */
    suspend fun deleteAllAccounts(): Int
    suspend fun updateFavorite(id: Long, favorite: Boolean)

    suspend fun accountExists(issuer:String,accountName:String): Boolean

    /** Saves several accounts in one transaction (used by import). */
    suspend fun insertAccounts(accounts: List<Account>)
}