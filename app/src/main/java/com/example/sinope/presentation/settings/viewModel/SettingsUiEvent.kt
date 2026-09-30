package com.example.sinope.presentation.settings.viewModel

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.example.sinope.core.common.SinopeSnackbarTone

sealed interface SettingsUiEvent {

    /** Ask the screen to open the system "save file" picker. */
    data class PickExportLocation(
        val suggestedFileName: String
    ) : SettingsUiEvent

    data class ShowMessage(
        @param:StringRes val messageRes: Int,
        val tone: SinopeSnackbarTone,
        val formatArgs: List<Any> = emptyList(),
    ) : SettingsUiEvent

    /** A message whose wording depends on [quantity] (one account vs. several). */
    data class ShowCountedMessage(
        @param:PluralsRes val pluralRes: Int,
        val quantity: Int,
        val tone: SinopeSnackbarTone,
    ) : SettingsUiEvent
}
