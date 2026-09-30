package com.example.sinope.presentation.addaccount


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sinope.core.common.BackBar
import com.example.sinope.core.common.RowDivider
import com.example.sinope.core.common.SinopeSnackbarHost
import com.example.sinope.core.common.showSinopeSnackbar
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.addaccount.components.AddTab
import com.example.sinope.presentation.addaccount.components.ManualEntryTab
import com.example.sinope.presentation.addaccount.components.ScanQrTab
import androidx.annotation.StringRes
import com.example.sinope.domain.usecases.account.ManualEntryValidation
import com.example.sinope.presentation.addaccount.viewModel.AddAccountEvent
import com.example.sinope.presentation.addaccount.viewModel.AddAccountUiEvent
import com.example.sinope.presentation.addaccount.viewModel.AddAccountViewModel
import com.example.sinope.R
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource

/**
 * Add Account screen: a "Scan QR Code" / "Enter Manually" tab pair. The QR tab runs a live camera
 * scanner; the manual tab is a real form validated and saved by [AddAccountViewModel].
 *
 * @param onAccountAdded called once the account is stored, for both tabs.
 */
@Composable
fun AddAccountScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onAccountAdded: () -> Unit = {},
    viewModel: AddAccountViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sinopeSnackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current


    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddAccountUiEvent.ShowMessage -> {
                    sinopeSnackbarHostState.showSinopeSnackbar(
                        resources.getString(event.messageRes),
                        event.tone,
                    )
                }

                // Emitted after the account is actually stored, for both QR and manual entry.
                AddAccountUiEvent.AccountSaved -> onAccountAdded()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SinopeColors.Background),
        ) {
            BackBar(title = stringResource(R.string.add_account_title), onBack = onBack)

            Row(modifier = Modifier.fillMaxWidth()) {
                AddTab(
                    stringResource(R.string.tab_scan_qr_code),
                    selected = selectedTab == 0,
                    modifier = Modifier.weight(1f)
                ) { selectedTab = 0 }
                AddTab(
                    stringResource(R.string.tab_enter_manually),
                    selected = selectedTab == 1,
                    modifier = Modifier.weight(1f)
                ) { selectedTab = 1 }
            }
            RowDivider()

            if (selectedTab == 0) {
                ScanQrTab(
                    onQrScanned = { value ->
                        viewModel.onEvent(
                            AddAccountEvent.QrCodeScanned(value)
                        )
                    }
                )
            } else {
                ManualEntryTab(
                    issuer = state.issuer,
                    accountName = state.accountName,
                    secret = state.secret,
                    digits = state.digits,
                    period = state.period,
                    errorMessage = state.manualError?.let { stringResource(manualErrorRes(it)) },
                    onIssuerChange = { viewModel.onEvent(AddAccountEvent.IssuerChanged(it)) },
                    onAccountNameChange = { viewModel.onEvent(AddAccountEvent.AccountNameChanged(it)) },
                    onSecretChange = { viewModel.onEvent(AddAccountEvent.SecretChanged(it)) },
                    onDigitsChange = { viewModel.onEvent(AddAccountEvent.DigitsChanged(it)) },
                    onPeriodChange = { viewModel.onEvent(AddAccountEvent.PeriodChanged(it)) },
                    onSave = { viewModel.onEvent(AddAccountEvent.SaveAccount) },
                )
            }

        }
        SinopeSnackbarHost(
            hostState = sinopeSnackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }

}


@Preview(
    name = "Add Account",
    showBackground = true,
    backgroundColor = 0xFF0A0A0F,
    widthDp = 300,
    heightDp = 620
)
@Composable
private fun AddAccountPreview() {
    AddAccountScreen()
}

/** Text belongs to the UI, so the domain's validation result is mapped to a string here. */
@StringRes
private fun manualErrorRes(validation: ManualEntryValidation): Int = when (validation) {
    ManualEntryValidation.MissingName -> R.string.manual_error_missing_name
    ManualEntryValidation.MissingSecret -> R.string.manual_error_missing_secret
    ManualEntryValidation.InvalidSecret, ManualEntryValidation.Valid -> R.string.manual_error_invalid_secret
}
