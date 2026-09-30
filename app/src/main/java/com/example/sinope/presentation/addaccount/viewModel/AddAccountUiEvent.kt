package com.example.sinope.presentation.addaccount.viewModel

import com.example.sinope.core.common.SinopeSnackbarTone
import androidx.annotation.StringRes

sealed interface AddAccountUiEvent {

    /** Anything the screen should say in a snackbar: duplicate, bad QR code, failed save. */
    data class ShowMessage(
        @param:StringRes val messageRes: Int,
        val tone: SinopeSnackbarTone = SinopeSnackbarTone.Info
    ) : AddAccountUiEvent

    /** The account is stored; the screen closes. */
    data object AccountSaved : AddAccountUiEvent
}
