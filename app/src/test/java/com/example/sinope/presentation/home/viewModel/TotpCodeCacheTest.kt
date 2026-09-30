package com.example.sinope.presentation.home.viewModel

import com.example.sinope.domain.model.Account
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The cache trades a stale code for saved work, so the tests that matter are the invalidation
 * ones: a code must never outlive its period, or survive an edit to the account that produced it.
 */
class TotpCodeCacheTest {

    private var generations = 0

    /** Stands in for the real generator, returning something that varies with its inputs. */
    private val cache = TotpCodeCache { account, currentTime ->
        generations++
        "${account.secret}-${account.digits}-${currentTime / 1000 / account.period}"
    }

    private fun account(
        id: Long = 1,
        secret: String = "JBSWY3DPEHPK3PXP",
        algorithm: String = "SHA1",
        digits: Int = 6,
        period: Int = 30,
    ) = Account(
        id = id, issuer = "GitHub", accountName = "dev@example.com", secret = secret,
        algorithm = algorithm, digits = digits, period = period, emoji = "🔐", color = 0L,
        favorite = false,
    )

    /** Epoch millis at [second], so windows are easy to straddle: period 30 flips at 30s. */
    private fun at(second: Long) = second * 1_000L

    @Test
    fun `generates once per period, however often the countdown ticks`() {
        val account = account()
        val codes = (0L..29L).map { cache.codeFor(account, at(it)) }

        assertEquals(1, generations)
        assertEquals(setOf(codes.first()), codes.toSet())
    }

    @Test
    fun `generates again once the period rolls over`() {
        val account = account()
        val before = cache.codeFor(account, at(29))
        val after = cache.codeFor(account, at(30))

        assertEquals(2, generations)
        assertEquals("JBSWY3DPEHPK3PXP-6-0", before)
        assertEquals("JBSWY3DPEHPK3PXP-6-1", after)
    }

    @Test
    fun `an edited secret is not served from the old entry`() {
        cache.codeFor(account(), at(0))
        val edited = cache.codeFor(account(secret = "NBSWY3DPEHPK3PXQ"), at(1))

        assertEquals(2, generations)
        assertEquals("NBSWY3DPEHPK3PXQ-6-0", edited)
    }

    @Test
    fun `an edited digit count or period is not served from the old entry`() {
        cache.codeFor(account(), at(0))
        cache.codeFor(account(digits = 8), at(1))
        cache.codeFor(account(period = 60), at(2))

        assertEquals(3, generations)
    }

    @Test
    fun `accounts are cached independently`() {
        cache.codeFor(account(id = 1), at(0))
        cache.codeFor(account(id = 2), at(0))
        cache.codeFor(account(id = 1), at(1))
        cache.codeFor(account(id = 2), at(1))

        assertEquals(2, generations)
    }

    @Test
    fun `an account removed from the vault stops being cached`() {
        val kept = account(id = 1)
        val removed = account(id = 2)
        cache.codeFor(kept, at(0))
        cache.codeFor(removed, at(0))
        generations = 0

        cache.retainOnly(listOf(kept))

        cache.codeFor(kept, at(1))
        assertEquals("the kept account should still be cached", 0, generations)

        cache.codeFor(removed, at(1))
        assertEquals("the removed account should have been evicted", 1, generations)
    }
}
