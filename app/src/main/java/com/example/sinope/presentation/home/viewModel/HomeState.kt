package com.example.sinope.presentation.home.viewModel

import com.example.sinope.presentation.model.AccountUi

data class HomeState (
    val isLoading: Boolean = false,
    val accounts: List<AccountUi> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val selectedAccountId: Long? = null
)
