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

        val (labelIssuer, accountName) = parseLabel(uri.pathSegments.firstOrNull())

        // The spec says the issuer query parameter wins over the label prefix
        val issuer = uri.getQueryParameter("issuer")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: labelIssuer
            ?: ""


        val algorithm = uri.getQueryParameter("algorithm")
            ?: "SHA1"

        val digits = uri.getQueryParameter("digits")
            ?.toIntOrNull()
            ?: 6

        val period = uri.getQueryParameter("period")
            ?.toIntOrNull()
            ?: 30

        return Account(
            issuer = issuer,
            accountName = accountName,
            secret = secret,
            algorithm = algorithm,
            digits = digits,
            period = period,
            emoji = "\uD83D\uDD10",
            color = SinopeColors.AccountColors.random().value.toLong(),
            favorite = false
        )


    }

    /**
     * Splits an otpauth label into (issuer, accountName).
     *
     * "GitHub:alice"   -> ("GitHub", "alice")
     * "GitHub: alice"  -> ("GitHub", "alice")
     * "alice"          -> (null, "alice")
     * null / blank     -> (null, "")
     */
    private fun parseLabel(label: String?): Pair<String?, String> {
        if (label.isNullOrBlank()) return null to ""

        if (!label.contains(":")) return null to label.trim()

        val issuer = label.substringBefore(":")
            .trim()
            .takeIf { it.isNotEmpty() }

        val accountName = label.substringAfter(":").trim()

        return issuer to accountName
    }
}

///otpauth://totp/Google?secret=egrhearhkerjkhfjsdhsjdfhouehf&algorithm=SHA1&digits=6&period=30