package com.example.sinope.presentation.editAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sinope.core.common.AccentPicker
import com.example.sinope.core.common.BackBar
import com.example.sinope.core.common.FieldLabel
import com.example.sinope.core.common.SinopeButton
import com.example.sinope.core.common.SinopeSnackbarHost
import com.example.sinope.core.common.SinopeSnackbarTone
import com.example.sinope.core.common.VaultTextField
import com.example.sinope.core.common.showSinopeSnackbar
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.domain.model.Account
import com.example.sinope.presentation.editAccount.components.AccountIdentityCard
import com.example.sinope.presentation.editAccount.components.DeleteAccountDialog
import com.example.sinope.presentation.editAccount.components.EditSegmentedRow
import com.example.sinope.presentation.editAccount.components.EmojiPicker
import com.example.sinope.presentation.editAccount.components.FavoriteRow
import com.example.sinope.presentation.editAccount.viewModel.EditAccountEvent
import com.example.sinope.presentation.editAccount.viewModel.EditAccountState
import com.example.sinope.presentation.editAccount.viewModel.EditAccountUiEvent
import com.example.sinope.presentation.editAccount.viewModel.EditAccountViewModel
import com.example.sinope.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * Edit Account screen: rename an account, restyle its avatar, pin it to Favorites and tune the
 * TOTP digits/period — or delete it outright. The secret itself is deliberately not editable;
 * changing it would silently produce wrong codes, so it stays read-only.
 */
@Composable
fun EditAccountScreen(
    modifier: Modifier = Modifier,
    viewModel: EditAccountViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onSave: () ->Unit =  {},
    onDeleted: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val changesSavedMessage = stringResource(R.string.changes_saved)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditAccountUiEvent.ShowMessage ->
                    snackbarHostState.showSinopeSnackbar(
                        context.getString(event.messageRes),
                        event.tone,
                    )

                EditAccountUiEvent.AccountSaved -> {
                    snackbarHostState.showSinopeSnackbar(
                        message = changesSavedMessage,
                        tone = SinopeSnackbarTone.Success,
                    )
                }

                EditAccountUiEvent.AccountDeleted -> onDeleted()
            }
        }
    }

    EditAccountContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onSave = onSave,
        modifier = modifier,
    )
}

/** Stateless body of [EditAccountScreen] so previews and tests can drive it directly. */
@Composable
internal fun EditAccountContent(
    state: EditAccountState,
    snackbarHostState: SnackbarHostState,
    onEvent: (EditAccountEvent) -> Unit,
    onBack: () -> Unit,
    onSave:() -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SinopeColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackBar(title = stringResource(R.string.edit_account_title), onBack = onBack)

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = SinopeColors.Cyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                ) {
                    AccountIdentityCard(
                        emoji = state.emoji,
                        issuer = state.issuer,
                        accountName = state.accountName,
                        accent = state.color,
                    )

                    Spacer(Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.account_info))
                    VaultTextField(
                        value = state.issuer,
                        onValueChange = { onEvent(EditAccountEvent.IssuerChanged(it)) },
                        placeholder = stringResource(R.string.issuer_placeholder),
                        icon = Icons.Outlined.Shield,
                    )
                    state.issuerError?.let { error ->
                        Text(
                            text = stringResource(error),
                            color = SinopeColors.Danger,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                        )
                    }
                    VaultTextField(
                        value = state.accountName,
                        onValueChange = { onEvent(EditAccountEvent.AccountNameChanged(it)) },
                        placeholder = stringResource(R.string.account_name_placeholder),
                        icon = Icons.Outlined.Person,
                    )

                    Spacer(Modifier.height(8.dp))

                    FieldLabel(stringResource(R.string.icon))
                    EmojiPicker(
                        selected = state.emoji,
                        accent = state.color,
                        onSelect = { onEvent(EditAccountEvent.EmojiSelected(it)) },
                    )

                    Spacer(Modifier.height(16.dp))

                    FieldLabel(stringResource(R.string.accent_color))
                    AccentPicker(
                        selected = state.color,
                        onSelect = { onEvent(EditAccountEvent.ColorSelected(it)) },
                    )

                    Spacer(Modifier.height(18.dp))

                    FavoriteRow(
                        favorite = state.favorite,
                        onToggle = { onEvent(EditAccountEvent.FavoriteToggled) },
                    )

                    Spacer(Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.advanced))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SinopeColors.InputBg)
                            .border(1.dp, SinopeColors.Border, RoundedCornerShape(16.dp)),
                    ) {
                        EditSegmentedRow(
                            label = stringResource(R.string.digits),
                            options = listOf(6, 8),
                            selected = state.digits,
                            onSelect = { onEvent(EditAccountEvent.DigitsChanged(it)) },
                            accent = state.color,
                            showDivider = true,
                        )
                        EditSegmentedRow(
                            label = stringResource(R.string.period),
                            options = listOf(30, 60),
                            selected = state.period,
                            onSelect = { onEvent(EditAccountEvent.PeriodChanged(it)) },
                            accent = state.color,
                            optionLabel = { "${it}s" },
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    SinopeButton(
                        text = stringResource(
                            if (state.isSaving) R.string.saving else R.string.save_changes,
                        ),
                        onClick = if (state.canSave) {
                            {
                                onEvent(EditAccountEvent.SaveAccount)

                            }
                        } else {
                            null
                        },
                        contentPadVer = 15.dp,
                        roundedCornerSize = 16.dp,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        backgroundColor = if (state.canSave) {
                            SinopeColors.ButtonGradient
                        } else {
                            SolidColor(SinopeColors.InputBg)
                        },
                        textColor = if (state.canSave) {
                            SinopeColors.OnBrand
                        } else {
                            SinopeColors.TextMuted
                        },
                        borderWidth = if (state.canSave) null else 1.dp,
                    )

                    Spacer(Modifier.height(24.dp))

                    FieldLabel(stringResource(R.string.section_danger_zone))
                    DeleteAccountButton(
                        onClick = { onEvent(EditAccountEvent.DeleteRequested) },
                    )
                }
            }
        }

        SinopeSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

    }

    if (state.showDeleteDialog) {
        DeleteAccountDialog(
            issuer = state.issuer.ifBlank { stringResource(R.string.this_account) },
            onConfirm = { onEvent(EditAccountEvent.DeleteConfirmed) },
            onDismiss = { onEvent(EditAccountEvent.DeleteDismissed) },
        )
    }
}

/** Read-only row explaining why the shared secret and algorithm can't be edited here. */
@Composable
private fun SecretLockedRow(algorithm: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            Icons.Outlined.Lock,
            null,
            tint = SinopeColors.TextMuted,
            modifier = Modifier.size(14.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.secret_algorithm, algorithm),
                color = SinopeColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.secret_locked),
                color = SinopeColors.TextMuted,
                fontSize = 10.sp,
            )
        }
        Text(
            text = stringResource(R.string.secret_masked),
            color = SinopeColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

/** Outlined destructive action that opens the confirmation dialog. */
@Composable
private fun DeleteAccountButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SinopeColors.Danger.copy(alpha = 0.08f))
            .border(1.dp, SinopeColors.Danger.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.DeleteOutline,
            null,
            tint = SinopeColors.Danger,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.delete_account_button),
            color = SinopeColors.Danger,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Preview(
    name = "Edit Account",
    showBackground = true,
    backgroundColor = 0xFF0A0A0F,
    widthDp = 300,
    heightDp = 780,
)
@Composable
private fun EditAccountPreview() {
    val account = Account(
        id = 1,
        issuer = "GitHub",
        accountName = "dev@example.com",
        secret = "JBSWY3DPEHPK3PXP",
        algorithm = "SHA1",
        digits = 6,
        period = 30,
        emoji = "🐱",
        color = SinopeColors.Cyan.value.toLong(),
        favorite = true,
    )

    EditAccountContent(
        state = EditAccountState(
            original = account,
            issuer = account.issuer,
            accountName = account.accountName,
            emoji = account.emoji,
            color = SinopeColors.Cyan,
            favorite = account.favorite,
            digits = account.digits,
            period = account.period,
            isLoading = false,
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        onBack = {},
        onSave = {}
    )
}
