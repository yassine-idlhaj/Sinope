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

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("UPDATE accounts set favorite = :favorite where id = :id")
    suspend fun updateFavorite(id: Long, favorite: Boolean)

    @Query("SELECT * FROM accounts WHERE secret = :secret LIMIT 1")
    suspend fun getAccountBySecret(secret:String): AccountEntity?
}