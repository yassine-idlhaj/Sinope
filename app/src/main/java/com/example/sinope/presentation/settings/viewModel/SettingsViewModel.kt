package com.example.sinope.presentation.settings.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.R
import com.example.sinope.core.common.SinopeSnackbarTone
import com.example.sinope.domain.usecases.backup.BackupPasswordValidation
import com.example.sinope.domain.usecases.account.AccountUseCases
import com.example.sinope.domain.usecases.backup.BackupUseCases
import com.example.sinope.domain.usecases.settings.security.biometricLock.BiometricLockUseCases
import com.example.sinope.domain.usecases.settings.security.screenshotProtection.ScreenshotProtectionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val biometricLockUseCases: BiometricLockUseCases,
    private val screenshotProtectionUseCases: ScreenshotProtectionUseCases,
    private val backupUseCases: BackupUseCases,
    private val accountUseCases: AccountUseCases,
): ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Held only between "password confirmed" and "file picked". Never in state, wiped after use.
    private var pendingExportPassword: CharArray? = null


    init {
        observeBiometricLock()
        observeScreenshotProtection()
        observeVaultCounts()
    }

    /** Keeps the stats card in step with the vault as accounts are added, starred or deleted. */
    private fun observeVaultCounts() {
        viewModelScope.launch {
            accountUseCases.getAccounts().collect { accounts ->
                _settingsState.update {
                    it.copy(
                        accountCount = accounts.size,
                        favoriteCount = accounts.count { account -> account.favorite },
                    )
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.ToggleBiometricLock -> {
                toggleBiometricLock(event.biometricLockEnabled)
            }
            is SettingsEvent.ToggleScreenshotProtection -> {
                toggleScreenshotProtection(event.screenshotProtectionEnabled)
            }
            SettingsEvent.ExportClicked -> startExport()

            SettingsEvent.DeleteAllRequested -> requestDeleteAll()
            SettingsEvent.DeleteAllConfirmed -> deleteAllAccounts()
            SettingsEvent.DeleteAllDismissed ->
                _settingsState.update { it.copy(showDeleteAllDialog = false) }
            SettingsEvent.ExportDialogDismissed -> {
                _settingsState.update { it.copy(showExportDialog = false, exportPasswordError = null) }
            }
            is SettingsEvent.ExportPasswordConfirmed -> {
                confirmExportPassword(event.password, event.confirmation)
            }
            is SettingsEvent.ExportLocationPicked -> exportTo(event.uri)
        }
    }

    /** Opens the confirmation, unless there is nothing to delete in the first place. */
    private fun requestDeleteAll() {
        viewModelScope.launch {
            if (_settingsState.value.accountCount == 0) {
                _uiEvent.emit(
                    SettingsUiEvent.ShowMessage(
                        R.string.delete_all_nothing_to_delete,
                        SinopeSnackbarTone.Info,
                    )
                )
            } else {
                _settingsState.update { it.copy(showDeleteAllDialog = true) }
            }
        }
    }

    /**
     * Empties the vault. The dialog closes first so the confirmation can't be tapped twice, and
     * [SettingsState.accountCount] falls to zero on its own — the counts come from the DB.
     */
    private fun deleteAllAccounts() {
        viewModelScope.launch {
            _settingsState.update { it.copy(showDeleteAllDialog = false, isDeletingAll = true) }

            try {
                accountUseCases.deleteAllAccounts()

                _uiEvent.emit(
                    SettingsUiEvent.ShowMessage(
                        R.string.delete_all_success,
                        SinopeSnackbarTone.Success,
                    )
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                Log.e(TAG, "Deleting all accounts failed", error)

                _uiEvent.emit(
                    SettingsUiEvent.ShowMessage(
                        R.string.delete_all_failed,
                        SinopeSnackbarTone.Error,
                    )
                )
            } finally {
                _settingsState.update { it.copy(isDeletingAll = false) }
            }
        }
    }

    private fun startExport() {
        viewModelScope.launch {
            if (backupUseCases.hasAccountsToExport()) {
                _settingsState.update { it.copy(showExportDialog = true, exportPasswordError = null) }
            } else {
                _uiEvent.emit(SettingsUiEvent.ShowMessage(R.string.export_no_accounts, SinopeSnackbarTone.Info))
            }
        }
    }

    private fun confirmExportPassword(passwordText: String, confirmationText: String) {
        val password = passwordText.toCharArray()
        val confirmation = confirmationText.toCharArray()

        val validation = backupUseCases.validateBackupPassword(password, confirmation)
        confirmation.fill('\u0000')

        if (validation != BackupPasswordValidation.Valid) {
            password.fill('\u0000')
            _settingsState.update { it.copy(exportPasswordError = validation) }
            return
        }

        clearPendingPassword()
        pendingExportPassword = password
        _settingsState.update { it.copy(showExportDialog = false, exportPasswordError = null) }

        viewModelScope.launch {
            _uiEvent.emit(SettingsUiEvent.PickExportLocation("sinope-backup-${LocalDate.now()}.sinope"))
        }
    }

    private fun exportTo(uri: String?) {
        val password = pendingExportPassword ?: return
        pendingExportPassword = null

        if (uri == null) {
            password.fill('\u0000')
            return
        }

        viewModelScope.launch {
            _settingsState.update { it.copy(isExporting = true) }
            try {
                val count = backupUseCases.exportBackup(password, uri)
                _uiEvent.emit(
                    SettingsUiEvent.ShowCountedMessage(R.plurals.export_success, count, SinopeSnackbarTone.Success)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Backup export failed", e)
                _uiEvent.emit(SettingsUiEvent.ShowMessage(R.string.export_failed, SinopeSnackbarTone.Error))
            } finally {
                password.fill('\u0000')
                _settingsState.update { it.copy(isExporting = false) }
            }
        }
    }

    private fun clearPendingPassword() {
        pendingExportPassword?.fill('\u0000')
        pendingExportPassword = null
    }

    override fun onCleared() {
        clearPendingPassword()
    }

    private fun toggleBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            biometricLockUseCases.saveBiometricLock(enabled)
        }
    }
    private fun observeBiometricLock() {
        viewModelScope.launch {
            biometricLockUseCases.readBiometricLock().collect { enabled ->
                _settingsState.value = _settingsState.value.copy(
                    biometricLockEnabled = enabled
                )
            }
        }
    }

    private fun toggleScreenshotProtection(enabled: Boolean) {
        viewModelScope.launch {
            screenshotProtectionUseCases.saveScreenshotProtection(enabled)
        }
    }

    private fun observeScreenshotProtection(){
        viewModelScope.launch {
            screenshotProtectionUseCases.readScreenshotProtection().collect { enabled ->
                _settingsState.value = _settingsState.value.copy(
                    screenshotProtectionEnabled = enabled
                )
            }
        }
    }
}
