package com.example.sinope.presentation.home.viewModel

import com.example.sinope.domain.model.Account

/**
 * Remembers the last code generated for each account.
 *
 * A TOTP only changes once per period, but the home list re-maps every second to advance the
 * countdown — so 29 of every 30 generations rebuild a string that is already on screen, and each
 * one decodes the secret and asks the JCA for a fresh Mac. Entries are keyed by the inputs that
 * actually change the code, so editing an account's secret, algorithm, digits or period shows the
 * new code on the next tick rather than a stale one.
 *
 * Holds one entry per account, and is confined to the view-model's collector coroutine.
 */
internal class TotpCodeCache(
    private val generate: (Account, Long) -> String,
) {

    private data class Inputs(
        val secret: String,
        val algorithm: String,
        val digits: Int,
        val period: Int,
        val window: Long,
    )

    private val entries = HashMap<Long, Pair<Inputs, String>>()

    /** The code for [account] at [currentTime], generated only if this period isn't cached yet. */
    fun codeFor(account: Account, currentTime: Long): String {
        val inputs = with(account) {
            Inputs(
                secret = secret,
                algorithm = algorithm,
                digits = digits,
                period = period,
                window = currentTime / 1000 / period,
            )
        }

        entries[account.id]?.let { (cached, code) ->
            if (cached == inputs) return code
        }

        return generate(account, currentTime).also { entries[account.id] = inputs to it }
    }

    /** Drops entries for accounts that have left [vault], so the map stays vault-sized. */
    fun retainOnly(vault: List<Account>) {
        if (entries.isEmpty()) return
        entries.keys.retainAll(vault.mapTo(HashSet()) { it.id })
    }
}
