package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.model.Account
import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.apache.commons.codec.binary.Base32
import java.util.concurrent.TimeUnit

class GenerateTotpCode {

    operator fun invoke(
        account: Account,
        timestamp: Long
    ):String{



        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = account.digits,
            hmacAlgorithm = getHmacAlgorithm(account),
            timeStep = account.period.toLong(),
            timeStepUnit = TimeUnit.SECONDS
        )

        val secret = Base32().decode(account.secret)



        return  TimeBasedOneTimePasswordGenerator(
            secret = secret,
            config = config,
        ).generate(timestamp)
    }

    private fun getHmacAlgorithm(account: Account) = when (account.algorithm.uppercase()) {
        "SHA1" -> HmacAlgorithm.SHA1
        "SHA256" -> HmacAlgorithm.SHA256
        "SHA512" -> HmacAlgorithm.SHA512
        else -> throw IllegalArgumentException(
            "Unsupported TOTP algorithm: ${account.algorithm}"
        )
    }
}