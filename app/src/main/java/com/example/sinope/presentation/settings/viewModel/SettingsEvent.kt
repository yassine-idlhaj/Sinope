package com.example.sinope.presentation.settings.viewModel

sealed interface SettingsEvent {

    data class ToggleBiometricLock(
        val biometricLockEnabled: Boolean
    ) : SettingsEvent

    data class ToggleScreenshotProtection(
        val screenshotProtectionEnabled: Boolean
    ) : SettingsEvent

    data object ExportClicked : SettingsEvent

    data object ExportDialogDismissed : SettingsEvent

    data class ExportPasswordConfirmed(
        val password: String,
        val confirmation: String,
    ) : SettingsEvent

    /** Result of the system "save file" picker; null when the user cancelled. */
    data class ExportLocationPicked(
        val uri: String?
    ) : SettingsEvent

    /** Danger zone: tapped "Delete All Accounts", which only opens the confirmation. */
    data object DeleteAllRequested : SettingsEvent

    /** The confirmation was accepted — this is the one that actually wipes the vault. */
    data object DeleteAllConfirmed : SettingsEvent

    data object DeleteAllDismissed : SettingsEvent
}
