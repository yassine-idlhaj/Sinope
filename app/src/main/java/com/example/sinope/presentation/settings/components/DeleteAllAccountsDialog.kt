package com.example.sinope.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.R
import com.example.sinope.core.utils.SinopeColors

/**
 * Confirmation for wiping the whole vault.
 *
 * Deliberately heavier than the single-account dialog: it names how many accounts are about to go
 * and points at the backup, because every secret here exists only on this device.
 */
@Composable
internal fun DeleteAllAccountsDialog(
    accountCount: Int,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        // A wipe in progress shouldn't be dismissable by tapping outside.
        onDismissRequest = { if (!isDeleting) onDismiss() },
        containerColor = SinopeColors.Surface,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SinopeColors.Danger.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.DeleteForever,
                    null,
                    tint = SinopeColors.Danger,
                    modifier = Modifier.size(18.dp),
                )
            }
        },
        title = {
            Text(
                text = pluralStringResource(
                    R.plurals.delete_all_dialog_title,
                    accountCount,
                    accountCount,
                ),
                color = SinopeColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.delete_all_dialog_body),
                    color = SinopeColors.TextSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = stringResource(R.string.delete_all_dialog_backup_hint),
                    color = SinopeColors.Amber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
        confirmButton = {
            DialogAction(
                text = if (isDeleting) {
                    stringResource(R.string.delete_all_in_progress)
                } else {
                    stringResource(R.string.delete_all_confirm)
                },
                onClick = onConfirm,
                filled = true,
                enabled = !isDeleting,
            )
        },
        dismissButton = {
            DialogAction(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                filled = false,
                enabled = !isDeleting,
            )
        },
    )
}

@Composable
private fun DialogAction(
    text: String,
    onClick: () -> Unit,
    filled: Boolean,
    enabled: Boolean,
) {
    val background = when {
        !filled -> SinopeColors.InputBg
        enabled -> SinopeColors.Danger
        else -> SinopeColors.Danger.copy(alpha = 0.4f)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = if (filled) background else SinopeColors.Border,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = text,
            color = if (filled) SinopeColors.Background else SinopeColors.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
