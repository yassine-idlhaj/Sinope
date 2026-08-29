package com.example.sinope.presentation.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sinope.core.common.SectionLabel
import com.example.sinope.core.common.SinopeButton
import com.example.sinope.core.common.SinopeSnackbarHost
import com.example.sinope.core.common.SinopeSnackbarTone
import com.example.sinope.core.common.showSinopeSnackbar
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.editAccount.components.DeleteAccountDialog
import com.example.sinope.presentation.home.components.AccountCard
import com.example.sinope.presentation.home.components.QuickActionItem
import com.example.sinope.presentation.home.components.SinopeHeader
import com.example.sinope.presentation.home.viewModel.HomeEvent
import com.example.sinope.presentation.home.viewModel.HomeViewModel
import com.example.sinope.presentation.model.AccountUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/**
 * Home / "VAULT" screen: gradient title, Favorites and All Accounts sections, and per-account
 * cards with a live countdown. UI only — the shared countdown is local state so previews feel
 * live; a real screen would source codes and remaining seconds from a ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onAddAccount: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onEditAccount: (String) -> Unit = {},
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedAccount by remember {
        mutableStateOf<AccountUi?>(null)
    }
    val sheetState = rememberModalBottomSheetState()
    val codeCopiedMessage = stringResource(R.string.code_copied)

    val favorites = state.accounts.filter { it.favorite }
    val others = state.accounts.filterNot { it.favorite }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SinopeColors.Background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                SinopeHeader(
                    accountCount = state.accounts.size, onOpenSettings = onOpenSettings
                )
            }

            if (favorites.isNotEmpty()) {
                item {
                    SectionLabel(
                        text = stringResource(R.string.favorites_section),
                        color = SinopeColors.Amber,
                        leading = {
                            Icon(
                                Icons.Filled.Star,
                                null,
                                tint = SinopeColors.Amber,
                                modifier = Modifier.size(11.dp)
                            )
                        },
                    )
                }
                items(items = favorites, key = { it.id }) {account ->
                    accountCard(
                        account = account,
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        viewModel = viewModel,
                        onLongClick = { currentAccount ->
                            selectedAccount = currentAccount
                        },
                        onEditAccount = onEditAccount
                    )
                }
            }

            item {
                SectionLabel(
                    text = stringResource(R.string.all_accounts_section),
                    color = SinopeColors.Cyan,
                    leading = {
                        Icon(
                            Icons.Outlined.Lock,
                            null,
                            tint = SinopeColors.Cyan,
                            modifier = Modifier.size(11.dp)
                        )
                    },
                )
            }
            items(others, key = { it.id }) { account ->
                accountCard(
                    account = account,
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    viewModel = viewModel,
                    onLongClick = { currentAccount ->
                        selectedAccount = currentAccount
                    },
                    onEditAccount = onEditAccount
                )
            }

            item {
                SinopeButton(
                    onClick = onAddAccount,
                    text = stringResource(R.string.add_account),
                    padHor = 13.dp,
                    padVer = 12.dp,
                    contentPadVer = 13.dp,
                    icon = Icons.Outlined.Add
                )
            }
        }

        selectedAccount?.let { account ->
            ModalBottomSheet(
                onDismissRequest = {
                    selectedAccount = null
                },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = account.issuer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SinopeColors.TextPrimary,
                    )

                    Spacer(Modifier.height(16.dp))

                    /// Copy Code
                    QuickActionItem(
                        icon = Icons.Outlined.ContentCopy,
                        text = stringResource(R.string.copy_code),
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSinopeSnackbar(
                                    message = codeCopiedMessage,
                                    tone = SinopeSnackbarTone.Success
                                )
                            }

                            selectedAccount = null
                        }
                    )

                    /// Fav
                    QuickActionItem(
                        icon = if (account.favorite) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.StarBorder
                        },
                        text = if (account.favorite) {
                            stringResource(R.string.remove_from_favorites)
                        } else {
                            stringResource(R.string.add_to_favorites)
                        },
                        onClick = {
                            viewModel.onEvent(
                                HomeEvent.ToggleFavorite(
                                    accountId = account.id.toLong(),
                                    favorite = !account.favorite
                                )
                            )

                            selectedAccount = null
                        }
                    )

                    /// Edit
                    QuickActionItem(
                        icon = Icons.Filled.Edit,
                        text = stringResource(R.string.edit_account),
                        onClick = {
                            onEditAccount(account.id)
                            selectedAccount = null
                        }
                    )

                    /// Delete
                    QuickActionItem(
                        icon = Icons.Filled.Delete,
                        text = stringResource(R.string.delete_account),
                        onClick = {
                            viewModel.onEvent(HomeEvent.DeleteRequested(account.id.toLong()))
                            selectedAccount = null
                        }
                    )
                }
            }
        }


        if (state.showDeleteDialog) {
            DeleteAccountDialog(
                issuer = stringResource(R.string.this_account),
                onConfirm = { viewModel.onEvent(HomeEvent.DeleteConfirmed) },
                onDismiss = { viewModel.onEvent(HomeEvent.DeleteDismissed) },
            )
        }

        SinopeSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun accountCard(
    account: AccountUi,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onLongClick: (AccountUi) -> Unit,
    viewModel: HomeViewModel,
    onEditAccount: (String) -> Unit = {},
){
    val codeCopiedMessage = stringResource(R.string.code_copied)

    AccountCard(
        account = account,
        onLongClick = {onLongClick(account)},
        onClick = { onEditAccount(account.id) },
        onCopied = {
            scope.launch {
                snackbarHostState.showSinopeSnackbar(
                    message = codeCopiedMessage,
                    tone = SinopeSnackbarTone.Success
                )
            }
        },
        onFavoriteClick = {
            viewModel.onEvent(
                HomeEvent.ToggleFavorite(
                    accountId = account.id.toLong(),
                    favorite = !account.favorite
                )
            )
        }
    )
}



@Preview(
    name = "Home",
    showBackground = true,
    backgroundColor = 0xFF0A0A0F,
    widthDp = 300,
    heightDp = 620
)
@Composable
private fun VaultHomePreview() {
    HomeScreen()
}
