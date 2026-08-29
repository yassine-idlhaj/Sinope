package com.example.sinope.presentation.editAccount.viewModel

import androidx.compose.ui.graphics.Color

sealed interface EditAccountEvent {

    data class IssuerChanged(val value: String) : EditAccountEvent
    data class AccountNameChanged(val value: String) : EditAccountEvent
    data class EmojiSelected(val value: String) : EditAccountEvent
    data class ColorSelected(val value: Color) : EditAccountEvent
    data class DigitsChanged(val value: Int) : EditAccountEvent
    data class PeriodChanged(val value: Int) : EditAccountEvent
    data object FavoriteToggled : EditAccountEvent

    data object SaveAccount : EditAccountEvent

    data object DeleteRequested : EditAccountEvent
    data object DeleteConfirmed : EditAccountEvent
    data object DeleteDismissed : EditAccountEvent
}
