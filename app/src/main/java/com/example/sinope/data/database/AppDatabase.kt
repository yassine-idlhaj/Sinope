package com.example.sinope.data.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.sinope.data.account.dao.AccountDao
import com.example.sinope.data.account.entity.AccountEntity

@Database(
    entities = [AccountEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun accountDao(): AccountDao

}