package com.example.sinope.presentation.addaccount.viewModel

import com.example.sinope.core.common.SinopeSnackbarTone
import androidx.annotation.StringRes

sealed interface AddAccountUiEvent {
    data class AccountAlreadyExists(
        @param:StringRes val messageRes: Int,
        val tone: SinopeSnackbarTone = SinopeSnackbarTone.Info
    ) : AddAccountUiEvent
}