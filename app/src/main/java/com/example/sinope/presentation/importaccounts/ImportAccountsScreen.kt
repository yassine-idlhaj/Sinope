package com.example.sinope.presentation.importaccounts

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sinope.R
import com.example.sinope.core.common.BackBar
import com.example.sinope.core.common.SinopeButton
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.importaccounts.components.ImportPasswordDialog
import com.example.sinope.presentation.importaccounts.viewModel.ImportCandidateUi
import com.example.sinope.presentation.importaccounts.viewModel.ImportEvent
import com.example.sinope.presentation.importaccounts.viewModel.ImportStage
import com.example.sinope.presentation.importaccounts.viewModel.ImportUiEvent
import com.example.sinope.presentation.importaccounts.viewModel.ImportViewModel

/**
 * Import flow: pick a .sinope file, unlock it, review what it holds, then save the selected
 * accounts. Nothing reaches the database until the user presses Import.
 */
@Composable
fun ImportAccountsScreen(
    onBack: () -> Unit,
    viewModel: ImportViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Storage Access Framework: read access to one file the user chose, no permission needed.
    // "*/*" because .sinope has no registered MIME type.
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        viewModel.onEvent(ImportEvent.FilePicked(uri?.toString()))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ImportUiEvent.OpenFilePicker -> filePickerLauncher.launch(arrayOf("*/*"))
                ImportUiEvent.NavigateBack -> onBack()
            }
        }
    }

    // Opens the picker automatically the first time the screen appears. The saveable flag keeps a
    // rotation from reopening it on top of the preview.
    var pickerLaunched by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!pickerLaunched && state.stage == ImportStage.PickingFile) {
            pickerLaunched = true
            filePickerLauncher.launch(arrayOf("*/*"))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SinopeColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackBar(title = stringResource(R.string.import_accounts), onBack = onBack)

            when (state.stage) {
                ImportStage.PickingFile,
                ImportStage.Reading,
                ImportStage.Importing -> CenteredProgress(
                    text = when (state.stage) {
                        ImportStage.Reading -> stringResource(R.string.import_decrypting)
                        ImportStage.Importing -> stringResource(R.string.import_saving)
                        else -> stringResource(R.string.import_waiting_for_file)
                    }
                )

                ImportStage.Password -> Spacer(Modifier.fillMaxSize())

                ImportStage.Preview -> PreviewList(
                    candidates = state.candidates,
                    onToggle = { viewModel.onEvent(ImportEvent.ToggleCandidate(it)) },
                    onToggleAll = { viewModel.onEvent(ImportEvent.ToggleSelectAll) },
                    onImport = { viewModel.onEvent(ImportEvent.ConfirmImport) },
                )

                ImportStage.Finished -> CenteredMessage(
                    icon = true,
                    message = pluralStringResource(R.plurals.import_success, state.importedCount, state.importedCount),
                    actionText = stringResource(R.string.done),
                    onAction = onBack,
                )

                ImportStage.Error -> CenteredMessage(
                    icon = false,
                    message = stringResource(state.errorRes ?: R.string.import_failed),
                    actionText = stringResource(R.string.import_pick_another_file),
                    onAction = { viewModel.onEvent(ImportEvent.PickAnotherFile) },
                )
            }
        }

        if (state.stage == ImportStage.Password) {
            ImportPasswordDialog(
                wrongPassword = state.wrongPassword,
                onConfirm = { viewModel.onEvent(ImportEvent.PasswordSubmitted(it)) },
                onDismiss = { viewModel.onEvent(ImportEvent.PasswordCancelled) },
            )
        }
    }
}

@Composable
private fun PreviewList(
    candidates: List<ImportCandidateUi>,
    onToggle: (Int) -> Unit,
    onToggleAll: () -> Unit,
    onImport: () -> Unit,
) {
    val selectedCount = candidates.count { it.selected }
    val duplicateCount = candidates.count { it.isDuplicate }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = pluralStringResource(R.plurals.import_found, candidates.size, candidates.size),
                color = SinopeColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            if (duplicateCount > 0) {
                Text(
                    text = pluralStringResource(R.plurals.import_duplicates_found, duplicateCount, duplicateCount),
                    color = SinopeColors.TextSecondary,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.import_select_all),
                color = SinopeColors.Cyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onToggleAll)
                    .padding(vertical = 4.dp),
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(candidates, key = { it.index }) { candidate ->
                CandidateRow(candidate = candidate, onToggle = { onToggle(candidate.index) })
            }
        }

        SinopeButton(
            text = pluralStringResource(R.plurals.import_action, selectedCount, selectedCount),
            onClick = if (selectedCount > 0) onImport else null,
            padHor = 16.dp,
            padVer = 14.dp,
            contentPadVer = 14.dp,
        )
    }
}

@Composable
private fun CandidateRow(candidate: ImportCandidateUi, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SinopeColors.Surface)
            .border(1.dp, SinopeColors.Border, RoundedCornerShape(14.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Checkbox(
            checked = candidate.selected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = SinopeColors.Cyan,
                uncheckedColor = SinopeColors.Border,
                checkmarkColor = SinopeColors.Background,
            ),
        )
        Text(text = candidate.emoji, fontSize = 18.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = candidate.issuer.ifEmpty { candidate.accountName },
                color = SinopeColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = candidate.accountName,
                color = SinopeColors.TextSecondary,
                fontSize = 10.sp,
            )
        }
        if (candidate.isDuplicate) {
            Text(
                text = stringResource(R.string.import_duplicate_badge),
                color = SinopeColors.Amber,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SinopeColors.Amber.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun CenteredProgress(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = SinopeColors.Cyan)
            Spacer(Modifier.height(12.dp))
            Text(text = text, color = SinopeColors.TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun CenteredMessage(
    icon: Boolean,
    message: String,
    actionText: String,
    onAction: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            if (icon) {
                Icon(
                    Icons.Outlined.TaskAlt,
                    contentDescription = null,
                    tint = SinopeColors.Green,
                    modifier = Modifier.size(42.dp),
                )
                Spacer(Modifier.height(12.dp))
            }
            Text(
                text = message,
                color = SinopeColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))
            SinopeButton(
                text = actionText,
                onClick = onAction,
                contentPadVer = 12.dp,
            )
        }
    }
}
