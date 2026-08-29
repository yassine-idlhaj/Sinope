package com.example.sinope.presentation.editAccount.viewModel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.core.common.SinopeSnackbarTone
import com.example.sinope.domain.usecases.account.AccountUseCases
import com.example.sinope.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sinope.R

/**
 * Backs the Edit Account screen. The account is read once to seed the form — the form is the
 * source of truth from then on, so a code refresh elsewhere can't stomp on what's being typed.
 */
@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val accountUseCases: AccountUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val accountId: Long =
        savedStateHandle.get<String>(Route.ACCOUNT_ID_ARG)?.toLongOrNull() ?: 0L

    private val _state = MutableStateFlow(EditAccountState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<EditAccountUiEvent>()
    val events = _events.asSharedFlow()

    init {
        loadAccount()
    }

    fun onEvent(event: EditAccountEvent) {
        when (event) {
            is EditAccountEvent.IssuerChanged ->
                _state.update { it.copy(issuer = event.value) }

            is EditAccountEvent.AccountNameChanged ->
                _state.update { it.copy(accountName = event.value) }

            is EditAccountEvent.EmojiSelected ->
                _state.update { it.copy(emoji = event.value) }

            is EditAccountEvent.ColorSelected ->
                _state.update { it.copy(color = event.value) }

            is EditAccountEvent.DigitsChanged ->
                _state.update { it.copy(digits = event.value) }

            is EditAccountEvent.PeriodChanged ->
                _state.update { it.copy(period = event.value) }

            EditAccountEvent.FavoriteToggled ->
                _state.update { it.copy(favorite = !it.favorite) }

            EditAccountEvent.SaveAccount -> save()

            EditAccountEvent.DeleteRequested ->
                _state.update { it.copy(showDeleteDialog = true) }

            EditAccountEvent.DeleteDismissed ->
                _state.update { it.copy(showDeleteDialog = false) }

            EditAccountEvent.DeleteConfirmed -> delete()
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            val account = accountUseCases.getAccount(accountId).first()

            if (account == null) {
                _state.update { it.copy(isLoading = false) }
                _events.emit(
                    EditAccountUiEvent.ShowMessage(R.string.account_not_found, SinopeSnackbarTone.Error)
                )
                return@launch
            }

            _state.update {
                it.copy(
                    original = account,
                    issuer = account.issuer,
                    accountName = account.accountName,
                    emoji = account.emoji,
                    color = Color(account.color.toULong()),
                    favorite = account.favorite,
                    digits = account.digits,
                    period = account.period,
                    isLoading = false,
                )
            }
        }
    }

    private fun save() {
        val current = _state.value
        val updated = current.toAccount()

        if (updated == null || current.issuerError != null) {
            viewModelScope.launch {
                _events.emit(
                    EditAccountUiEvent.ShowMessage(
                        current.issuerError ?: R.string.nothing_to_save,
                        SinopeSnackbarTone.Error,
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching { accountUseCases.updateAccount(updated) }
                .onSuccess {
                    _state.update { it.copy(original = updated, isSaving = false) }
                    _events.emit(EditAccountUiEvent.AccountSaved)

                }
                .onFailure {
                    _state.update { it.copy(isSaving = false) }
                    _events.emit(
                        EditAccountUiEvent.ShowMessage(
                            R.string.couldnt_save_changes,
                            SinopeSnackbarTone.Error,
                        )
                    )
                }
        }
    }

    private fun delete() {
        val account = _state.value.original ?: return

        viewModelScope.launch {
            _state.update { it.copy(showDeleteDialog = false) }
            runCatching { accountUseCases.deleteAccount(account) }
                .onSuccess { _events.emit(EditAccountUiEvent.AccountDeleted) }
                .onFailure {
                    _events.emit(
                        EditAccountUiEvent.ShowMessage(
                            R.string.couldnt_delete_account,
                            SinopeSnackbarTone.Error,
                        )
                    )
                }
        }
    }
}
