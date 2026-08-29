package com.example.sinope.domain.usecases.account

import androidx.core.net.toUri
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.domain.model.Account

class ParseQrCode {

    operator fun invoke(value:String): Account{
        val uri = value.toUri()

        require(uri.scheme == "otpauth"){
            "Invalid OTPAuth URI"
        }


        require(uri.host == "totp"){
            "Invalid OTPAuth URI"
        }

        val secret = uri.getQueryParameter("secret") ?: throw IllegalArgumentException("Missing secret parameter")

        val algorithm = uri.getQueryParameter("algorithm")
            ?: "SHA1"

        val digits = uri.getQueryParameter("digits")
            ?.toIntOrNull()
            ?: 6

        val period = uri.getQueryParameter("period")
            ?.toIntOrNull()
            ?: 30

        return Account(
            issuer = uri.pathSegments.firstOrNull() ?: "",
            accountName = "",
            secret = secret,
            algorithm = algorithm,
            digits = digits,
            period = period,
            emoji = "\uD83D\uDD10",
            color = SinopeColors.AccountColors.random().value.toLong(),
            favorite = false
        )


    }
}

///otpauth://totp/Google?secret=egrhearhkerjkhfjsdhsjdfhouehf&algorithm=SHA1&digits=6&period=30