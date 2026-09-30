package com.example.sinope.presentation.importaccounts.viewModel

sealed interface ImportUiEvent {

    /** Ask the screen to open the system file picker. */
    data object OpenFilePicker : ImportUiEvent

    data object NavigateBack : ImportUiEvent
}
