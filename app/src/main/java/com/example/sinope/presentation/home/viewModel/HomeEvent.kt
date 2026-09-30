package com.example.sinope.presentation.home.viewModel

sealed interface HomeEvent {

    data class ToggleFavorite(
        val accountId: Long,
        val favorite: Boolean
    ) : HomeEvent
    data class DeleteRequested(
        val accountID : Long
    ) : HomeEvent
    data object DeleteConfirmed : HomeEvent
    data object DeleteDismissed : HomeEvent

    /** Reveals the search field. */
    data object SearchOpened : HomeEvent

    /** Hides the search field and drops the query, so the full vault comes back. */
    data object SearchClosed : HomeEvent

    data class SearchQueryChanged(
        val query: String
    ) : HomeEvent
}
