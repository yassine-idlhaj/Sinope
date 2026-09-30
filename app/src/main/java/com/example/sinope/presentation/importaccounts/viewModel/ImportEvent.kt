package com.example.sinope.presentation.importaccounts.viewModel

sealed interface ImportEvent {

    /** Result of the system file picker; null when the user cancelled. */
    data class FilePicked(val uri: String?) : ImportEvent

    data class PasswordSubmitted(val password: String) : ImportEvent

    data object PasswordCancelled : ImportEvent

    data class ToggleCandidate(val index: Int) : ImportEvent

    data object ToggleSelectAll : ImportEvent

    data object ConfirmImport : ImportEvent

    data object PickAnotherFile : ImportEvent
}
