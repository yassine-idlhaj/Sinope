package com.example.sinope.presentation.editAccount.viewModel

import com.example.sinope.core.common.SinopeSnackbarTone
import androidx.annotation.StringRes

/** One-shot effects the Edit Account screen reacts to: a snackbar, or leaving the screen. */
sealed interface EditAccountUiEvent {

    data class ShowMessage(
        @param:StringRes val messageRes: Int,
        val tone: SinopeSnackbarTone = SinopeSnackbarTone.Info,
    ) : EditAccountUiEvent

    data object AccountSaved : EditAccountUiEvent
    data object AccountDeleted : EditAccountUiEvent
}
