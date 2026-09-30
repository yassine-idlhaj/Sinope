package com.example.sinope.presentation.settings.viewModel

import com.example.sinope.domain.usecases.backup.BackupPasswordValidation

data class SettingsState(
    /** Vault size, shown in the stats card at the top of the screen. */
    val accountCount: Int = 0,
    val favoriteCount: Int = 0,
    val biometricLockEnabled: Boolean = false,
    val screenshotProtectionEnabled: Boolean = false,
    val showExportDialog: Boolean = false,
    val exportPasswordError: BackupPasswordValidation? = null,
    val isExporting: Boolean = false,
    val showDeleteAllDialog: Boolean = false,
    val isDeletingAll: Boolean = false,
)
