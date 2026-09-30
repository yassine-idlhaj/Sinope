package com.example.sinope.domain.usecases.app_entry

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.usecases.account.GenerateTotpCode
import org.junit.Test

class GenerateTotpCodeTest {

    @Test
    fun generateTotpCode() {
        val account = Account(
            id = 9,
            issuer = "Google",
            accountName = "",
            secret = "JBSWY3DPEHPK3PXP",
            algorithm = "SHA1",
            digits = 6,
            period = 30,
            emoji = "🔐",
            color = 0L,
            favorite = false,
        )

        val useCase = GenerateTotpCode()

        val timestamp = System.currentTimeMillis()

        val code = useCase(
            account = account,
            timestamp = timestamp
        )

        println("TOTP = $code")

        assert(code.length == account.digits)
        assert(code.all { it.isDigit() })
    }
}