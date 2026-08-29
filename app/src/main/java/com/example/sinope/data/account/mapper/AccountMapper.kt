package com.example.sinope.data.account.mapper

import com.example.sinope.data.account.entity.AccountEntity
import com.example.sinope.domain.model.Account


fun Account.toEntity(): AccountEntity =
     AccountEntity(
         id = id,
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

fun AccountEntity.toDomain(): Account =
    Account(
        id = id,
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