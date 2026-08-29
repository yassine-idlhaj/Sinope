package com.example.sinope.presentation.addaccount.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.domain.usecases.account.AccountUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sinope.R


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
            is AddAccountEvent.QrCodeScanned -> {
                viewModelScope.launch {
                    val account = accountUseCases.parseQrCode(event.value)
                    ///     TODO("Handle Errors in UI like if an error thrown from ParseQrCode, Catch it")
                    Log.d("Code",account.toString())

                    val accountExists = accountUseCases.isAccountExists(account.secret)

                    if(accountExists){
                        _uiEvent.emit(AddAccountUiEvent.AccountAlreadyExists(
                            messageRes = R.string.account_already_exists,
                        ))

                        return@launch
                    }

                    _state.update {
                        it.copy(isSaving = true)
                    }

                    accountUseCases.insertAccount(account)

                    _state.update {
                        it.copy(isSaving = false)
                    }
                }
            }
            else -> {
                Log.d("Event",event.toString())
            }
        }
    }
}