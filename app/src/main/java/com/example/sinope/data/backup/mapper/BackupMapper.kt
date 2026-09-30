package com.example.sinope.data.backup.mapper

import com.example.sinope.data.backup.model.BackupAccount
import com.example.sinope.domain.model.Account

fun Account.toBackupAccount(): BackupAccount =
    BackupAccount(
        issuer = issuer,
        accountName = accountName,
        secret = secret,
        algorithm = algorithm,
        digits = digits,
        period = period,
        emoji = emoji,
        color = color,
        favorite = favorite
    )

// id is left at its default (0) so Room assigns a new one on import.
fun BackupAccount.toDomain(): Account =
    Account(
        issuer = issuer,
        accountName = accountName,
        secret = secret,
        algorithm = algorithm,
        digits = digits,
        period = period,
        emoji = emoji,
        color = color,
        favorite = favorite
    )
