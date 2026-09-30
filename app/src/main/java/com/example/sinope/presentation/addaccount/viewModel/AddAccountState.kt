package com.example.sinope.presentation.addaccount.viewModel

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.usecases.account.ManualEntryValidation

data class AddAccountState(
  val scannedCode: String? = null,
  val scannedAccount: Account? = null,
  val isSaving: Boolean = false,
  val error: String? = null,

  // Manual entry form
  val issuer: String = "",
  val accountName: String = "",
  val secret: String = "",
  val digits: Int = 6,
  val period: Int = 30,
  val manualError: ManualEntryValidation? = null,
)
