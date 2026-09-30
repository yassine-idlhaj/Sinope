package com.example.sinope.presentation.importaccounts.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.R
import com.example.sinope.domain.model.Account
import com.example.sinope.domain.usecases.backup.BackupUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.crypto.AEADBadTagException
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val backupUseCases: BackupUseCases,
) : ViewModel() {

    companion object {
        private const val TAG = "ImportViewModel"
    }

    private val _state = MutableStateFlow(ImportState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ImportUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var sourceUri: String? = null

    // Decrypted accounts, held only while the preview is open. Dropped in onCleared().
    private var parsedAccounts: List<Account> = emptyList()

    fun onEvent(event: ImportEvent) {
        when (event) {
            is ImportEvent.FilePicked -> onFilePicked(event.uri)
            is ImportEvent.PasswordSubmitted -> readFile(event.password)
            ImportEvent.PasswordCancelled -> navigateBack()
            is ImportEvent.ToggleCandidate -> toggleCandidate(event.index)
            ImportEvent.ToggleSelectAll -> toggleSelectAll()
            ImportEvent.ConfirmImport -> importSelected()
            ImportEvent.PickAnotherFile -> {
                _state.update { ImportState(stage = ImportStage.PickingFile) }
                viewModelScope.launch { _uiEvent.emit(ImportUiEvent.OpenFilePicker) }
            }
        }
    }

    private fun onFilePicked(uri: String?) {
        if (uri == null) {
            navigateBack()
            return
        }
        sourceUri = uri
        _state.update { it.copy(stage = ImportStage.Password, wrongPassword = false) }
    }

    private fun readFile(passwordText: String) {
        val uri = sourceUri ?: return
        val password = passwordText.toCharArray()

        viewModelScope.launch {
            _state.update { it.copy(stage = ImportStage.Reading, wrongPassword = false) }
            try {
                val accounts = backupUseCases.readBackupFile(uri, password)
                val candidates = backupUseCases.findImportCandidates(accounts)

                parsedAccounts = accounts
                _state.update {
                    it.copy(
                        stage = ImportStage.Preview,
                        candidates = candidates.mapIndexed { index, candidate ->
                            ImportCandidateUi(
                                index = index,
                                issuer = candidate.account.issuer,
                                accountName = candidate.account.accountName,
                                emoji = candidate.account.emoji,
                                isDuplicate = candidate.isDuplicate,
                                // Duplicates start unchecked so nothing is added twice by accident.
                                selected = !candidate.isDuplicate,
                            )
                        },
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: AEADBadTagException) {
                // Wrong password, or the file was modified.
                _state.update { it.copy(stage = ImportStage.Password, wrongPassword = true) }
            } catch (e: Exception) {
                Log.e(TAG, "Reading backup failed", e)
                _state.update { it.copy(stage = ImportStage.Error, errorRes = R.string.import_invalid_file) }
            } finally {
                password.fill('\u0000')
            }
        }
    }

    private fun toggleCandidate(index: Int) {
        _state.update { state ->
            state.copy(
                candidates = state.candidates.map {
                    if (it.index == index) it.copy(selected = !it.selected) else it
                }
            )
        }
    }

    private fun toggleSelectAll() {
        _state.update { state ->
            val selectAll = state.candidates.any { !it.selected }
            state.copy(candidates = state.candidates.map { it.copy(selected = selectAll) })
        }
    }

    private fun importSelected() {
        val selectedIndexes = _state.value.candidates.filter { it.selected }.map { it.index }.toSet()
        val selected = parsedAccounts.filterIndexed { index, _ -> index in selectedIndexes }

        if (selected.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(stage = ImportStage.Importing) }
            try {
                val count = backupUseCases.importAccounts(selected)
                parsedAccounts = emptyList()
                _state.update { it.copy(stage = ImportStage.Finished, importedCount = count, candidates = emptyList()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Importing accounts failed", e)
                _state.update { it.copy(stage = ImportStage.Error, errorRes = R.string.import_failed) }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch { _uiEvent.emit(ImportUiEvent.NavigateBack) }
    }

    override fun onCleared() {
        parsedAccounts = emptyList()
    }
}
