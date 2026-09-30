package com.example.sinope.presentation.home.viewModel

import com.example.sinope.presentation.model.AccountUi

data class HomeState (
    val isLoading: Boolean = false,
    /** The accounts to show — already narrowed to [searchQuery] by the view-model. */
    val accounts: List<AccountUi> = emptyList(),
    /** Size of the whole vault, so the header count doesn't shrink while a search is running. */
    val totalAccounts: Int = 0,
    val showDeleteDialog: Boolean = false,
    val selectedAccountId: Long? = null,
    /** Whether the header's search field is open. Closing it always clears [searchQuery]. */
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
) {

    /** True once a real query matches nothing, so the screen can say so instead of showing a void. */
    val hasNoSearchResults: Boolean
        get() = searchQuery.isNotBlank() && accounts.isEmpty()
}
