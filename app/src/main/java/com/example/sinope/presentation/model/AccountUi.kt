package com.example.sinope.presentation.model

import androidx.compose.ui.graphics.Color
import com.example.sinope.core.utils.SinopeColors

data class AccountUi(
    val id: String,
    val issuer: String,
    val email: String,
    val emoji: String,
    val code: String,
    val color: Color,
    val favorite: Boolean,
    val secondsLeft: Int,
    val period: Int,
)

/** Hard-coded accounts used by the previews and as the default for [com.example.sinope.presentation.home.HomeScreen]. */
val sampleAccounts = listOf(
    AccountUi("1", "GitHub", "dev@example.com", "🐱", "523 891", SinopeColors.Cyan, favorite = false, secondsLeft = 23, period = 30),
    AccountUi("2", "Google", "me@gmail.com", "🔍", "738 204", SinopeColors.Violet, favorite = false, secondsLeft = 23, period = 30),
    AccountUi("3", "Microsoft", "work@company.com", "🪟", "192 047", SinopeColors.Pink, favorite = false, secondsLeft = 23, period = 30),
//    AccountUi("4", "Coinbase", "crypto@wallet.io", "🪙", "867 530", SinopeColors.Green, favorite = false),
)
