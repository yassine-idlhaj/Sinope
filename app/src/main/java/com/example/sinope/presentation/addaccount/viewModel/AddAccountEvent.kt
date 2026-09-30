package com.example.sinope.presentation.addaccount.viewModel

sealed interface AddAccountEvent {

    data class AccountNameChanged(val value: String) : AddAccountEvent
    data class IssuerChanged(val value: String) : AddAccountEvent
    data class SecretChanged(val value: String) : AddAccountEvent
    data object UseScannedCode : AddAccountEvent

    data object ScanAgain : AddAccountEvent

    data class QrCodeScanned(
        val value: String
    ) : AddAccountEvent

    data class DigitsChanged(val value: Int) : AddAccountEvent

    data class PeriodChanged(val value: Int) : AddAccountEvent

    data object SaveAccount : AddAccountEvent

}