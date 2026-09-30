package com.example.sinope.presentation.home.viewModel

import com.example.sinope.core.utils.foldForSearch
import com.example.sinope.domain.model.Account

/**
 * Narrows the vault to the accounts matching [query], on the issuer or the account name, so "git"
 * and "dev@" both find the same GitHub entry. A blank query means the whole vault.
 *
 * Deliberately takes domain [Account]s rather than the UI models: the home list re-maps itself
 * every second to refresh the codes, and folding text for accounts nobody can see is wasted work.
 */
internal fun List<Account>.filterBySearch(query: String): List<Account> {
    val folded = query.trim().foldForSearch()
    if (folded.isEmpty()) return this

    return filter { account ->
        account.issuer.foldForSearch().contains(folded) ||
                account.accountName.foldForSearch().contains(folded)
    }
}
