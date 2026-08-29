package com.example.sinope.data.account.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issuer: String,
    val accountName: String,
    val secret: String,
    val algorithm: String,
    val digits: Int,
    val period: Int,

    val emoji: String = "\uD83D\uDD10",
    val color: Long = 0L,
    val favorite: Boolean = false,
)