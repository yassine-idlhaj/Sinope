package com.example.sinope.presentation.home.viewModel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.domain.usecases.account.AccountUseCases
import com.example.sinope.presentation.model.AccountUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountUseCase: AccountUseCases,
): ViewModel(){

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime = _currentTime.asStateFlow()

    init {
        observeAccounts()
        viewModelScope.launch {
            while (true) {
                _currentTime.value = System.currentTimeMillis()
                delay(1_000.milliseconds)
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    accountUseCase.toggleFavorite(
                        id = event.accountId,
                        favorite = event.favorite
                    )
                }
            }

            is HomeEvent.DeleteRequested -> {
                _state.update { it.copy(showDeleteDialog = true, selectedAccountId = event.accountID) }
            }
            HomeEvent.DeleteConfirmed -> delete()
            HomeEvent.DeleteDismissed -> _state.update { it.copy(showDeleteDialog = false) }
        }
    }

    private fun observeAccounts(){
        viewModelScope.launch {
            combine(
                accountUseCase.getAccounts(),
                currentTime,
            ){accounts, currentTime ->
                accounts.map { account ->

                    val code = accountUseCase.generateTotpCode(
                        account,
                        currentTime
                    )

                    val secondsLeft =
                        account.period - ((currentTime / 1000) % account.period).toInt()

                    val uiColor = Color(account.color)


                    AccountUi(
                        id = account.id.toString(),
                        issuer = account.issuer,
                        email = account.accountName,
                        emoji = account.emoji,
                        code = code,
                        color = Color(account.color.toULong()),
                        favorite = account.favorite,
                        secondsLeft = secondsLeft,
                        period = account.period
                    )
                }
            }.collect { accounts ->
                _state.update{
                    it.copy(accounts = accounts)
                }
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            val accountId = _state.value.selectedAccountId
                ?: return@launch

            Log.d("Account", "Deleting account: $accountId")

            accountUseCase.getAccount(accountId).collect { account ->

                if (account == null) {
                    Log.d("Account", "Account not found: $accountId")
                    return@collect
                }

                Log.d("Account", "Account found: $account")

                accountUseCase.deleteAccount(account)

                _state.update {
                    it.copy(
                        showDeleteDialog = false,
                        selectedAccountId = null
                    )
                }
            }
        }
    }
}