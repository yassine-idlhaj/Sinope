package com.example.sinope.domain.model

data class Account(
    val id: Long = 0,
    val issuer: String,
    val accountName: String,
    val secret: String,
    val algorithm: String,
    val digits: Int,
    val period: Int,
    val emoji: String,
    val color: Long,
    val favorite: Boolean,
)
