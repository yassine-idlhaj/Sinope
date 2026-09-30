package com.example.sinope.presentation.home.viewModel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.domain.model.Account
import com.example.sinope.domain.usecases.account.AccountUseCases
import com.example.sinope.presentation.model.AccountUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
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

    private val codeCache = TotpCodeCache { account, currentTime ->
        accountUseCase.generateTotpCode(account, currentTime)
    }

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

            HomeEvent.SearchOpened -> _state.update { it.copy(isSearchActive = true) }

            // Closing also clears the query: a hidden filter would silently hide accounts.
            HomeEvent.SearchClosed -> _state.update {
                it.copy(isSearchActive = false, searchQuery = "")
            }

            is HomeEvent.SearchQueryChanged -> _state.update { it.copy(searchQuery = event.query) }
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            val query = _state
                .map { it.searchQuery }
                .distinctUntilChanged()

            // Two nested combines, not one three-way combine: the clock ticks every second, and
            // folding text for the search and hashing codes for accounts the query already ruled
            // out is work nobody sees. Filtering sits in the outer combine so it re-runs only when
            // the vault or the query actually changes.
            val matching = combine(
                accountUseCase.getAccounts(),
                query,
            ) { accounts, currentQuery ->
                codeCache.retainOnly(accounts)

                VaultSnapshot(
                    total = accounts.size,
                    matching = accounts.filterBySearch(currentQuery),
                )
            }

            combine(matching, currentTime) { snapshot, currentTime ->
                snapshot to snapshot.matching.map { it.toAccountUi(currentTime) }
            }.collect { (snapshot, accounts) ->
                _state.update {
                    it.copy(accounts = accounts, totalAccounts = snapshot.total)
                }
            }
        }
    }

    /** The vault after the search filter, plus how big it was before — the header shows the total. */
    private data class VaultSnapshot(
        val total: Int,
        val matching: List<Account>,
    )

    /** Builds the row model for [this] account as of [currentTime], code and countdown included. */
    private fun Account.toAccountUi(currentTime: Long) = AccountUi(
        id = id.toString(),
        issuer = issuer,
        email = accountName,
        emoji = emoji,
        code = codeCache.codeFor(this, currentTime),
        color = Color(color.toULong()),
        favorite = favorite,
        secondsLeft = period - ((currentTime / 1000) % period).toInt(),
        period = period,
    )

    private fun delete() {
        val accountId = _state.value.selectedAccountId ?: return

        viewModelScope.launch {
            // first(): take one value and stop watching. collect would keep a Room observer alive.
            val account = accountUseCase.getAccount(accountId).first()

            if (account != null) {
                accountUseCase.deleteAccount(account)
            }

            // Always close the dialog, even if the account had already disappeared.
            _state.update {
                it.copy(
                    showDeleteDialog = false,
                    selectedAccountId = null
                )
            }
        }
    }
}