package com.example.sinope.data.account.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.sinope.data.account.entity.AccountEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts")
    fun getAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccount(id: Long): Flow<AccountEntity?>

    @Insert
    suspend fun insertAccount(account: AccountEntity)

    /** Room runs a list insert inside a single transaction: all rows land, or none do. */
    @Insert
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("UPDATE accounts set favorite = :favorite where id = :id")
    suspend fun updateFavorite(id: Long, favorite: Boolean)

    @Query("SELECT EXISTS (SELECT 1 FROM accounts WHERE issuer = :issuer COLLATE NOCASE AND accountName = :accountName COLLATE NOCASE)")
    suspend fun accountExists(issuer:String,accountName:String): Boolean

    @Query("SELECT * FROM accounts")
    suspend fun getAccountsOnce(): List<AccountEntity>

    @Query("UPDATE accounts SET secret = :secret WHERE id = :id")
    suspend fun updateSecret(id: Long, secret: String)

    /** Empties the table in one statement. Returns how many rows went. */
    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccounts(): Int
}