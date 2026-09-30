package com.example.sinope.presentation.addaccount.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.R
import com.example.sinope.core.common.SinopeSnackbarTone
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.domain.model.Account
import com.example.sinope.domain.usecases.account.AccountUseCases
import com.example.sinope.domain.usecases.account.ManualEntryValidation
import com.example.sinope.domain.usecases.account.normalizeSecret
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val accountUseCases: AccountUseCases
): ViewModel() {


    private val _state = MutableStateFlow(AddAccountState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddAccountUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()




    fun onEvent(event: AddAccountEvent){
        when(event){
            is AddAccountEvent.QrCodeScanned -> saveScannedAccount(event.value)

            is AddAccountEvent.IssuerChanged ->
                _state.update { it.copy(issuer = event.value, manualError = null) }

            is AddAccountEvent.AccountNameChanged ->
                _state.update { it.copy(accountName = event.value, manualError = null) }

            is AddAccountEvent.SecretChanged ->
                _state.update { it.copy(secret = event.value, manualError = null) }

            is AddAccountEvent.DigitsChanged ->
                _state.update { it.copy(digits = event.value) }

            is AddAccountEvent.PeriodChanged ->
                _state.update { it.copy(period = event.value) }

            AddAccountEvent.SaveAccount -> saveManualAccount()

            else -> {
                Log.d("Event",event.toString())
            }
        }
    }

    private fun saveScannedAccount(payload: String) {
        viewModelScope.launch {
            val account = try {
                accountUseCases.parseQrCode(payload)
            } catch (e: Exception) {
                // Never log the payload itself: it carries the secret.
                Log.w(TAG, "Unrecognised QR payload", e)
                _uiEvent.emit(
                    AddAccountUiEvent.ShowMessage(R.string.qr_invalid_code, SinopeSnackbarTone.Error)
                )
                return@launch
            }

            if (accountUseCases.isAccountExists(account.issuer, account.accountName)) {
                _uiEvent.emit(AddAccountUiEvent.ShowMessage(R.string.account_already_exists))
                return@launch
            }

            insertAndClose(account)
        }
    }

    /** Shared by both tabs: save, tell the screen to close, and never crash on failure. */
    private suspend fun insertAndClose(account: Account) {
        _state.update { it.copy(isSaving = true) }
        try {
            accountUseCases.insertAccount(account)
            _uiEvent.emit(AddAccountUiEvent.AccountSaved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Saving the account failed", e)
            _uiEvent.emit(
                AddAccountUiEvent.ShowMessage(R.string.account_save_failed, SinopeSnackbarTone.Error)
            )
        } finally {
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun saveManualAccount() {
        val form = _state.value

        val validation = accountUseCases.validateManualEntry(form.issuer, form.accountName, form.secret)
        if (validation != ManualEntryValidation.Valid) {
            _state.update { it.copy(manualError = validation) }
            return
        }

        viewModelScope.launch {
            val issuer = form.issuer.trim()
            val accountName = form.accountName.trim()

            if (accountUseCases.isAccountExists(issuer, accountName)) {
                _uiEvent.emit(AddAccountUiEvent.ShowMessage(R.string.account_already_exists))
                return@launch
            }

            insertAndClose(
                Account(
                    issuer = issuer,
                    accountName = accountName,
                    secret = normalizeSecret(form.secret),
                    algorithm = "SHA1",
                    digits = form.digits,
                    period = form.period,
                    emoji = "🔐",
                    color = SinopeColors.AccountColors.random().value.toLong(),
                    favorite = false,
                )
            )
        }
    }

    companion object {
        private const val TAG = "AddAccountViewModel"
    }
}