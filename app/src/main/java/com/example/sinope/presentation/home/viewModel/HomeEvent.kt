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
}