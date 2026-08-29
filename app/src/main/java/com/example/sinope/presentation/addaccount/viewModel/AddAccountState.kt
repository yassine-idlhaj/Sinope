package com.example.sinope.presentation.addaccount.viewModel

import com.example.sinope.domain.model.Account

data class AddAccountState(
  val scannedCode: String? = null,
  val scannedAccount: Account? = null,
  val isSaving: Boolean = false,
  val error: String? = null
)
