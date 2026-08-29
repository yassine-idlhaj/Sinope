package com.example.sinope.presentation.editAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/**
 * Confirmation for an irreversible delete: the secret only lives on this device, so removing the
 * account means re-scanning the QR code to get it back.
 */
@Composable
internal fun DeleteAccountDialog(
    issuer: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                    Icons.Outlined.DeleteOutline,
                    null,
                    tint = SinopeColors.Danger,
                    modifier = Modifier.size(18.dp),
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.delete_account_dialog_title, issuer),
                color = SinopeColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_account_dialog_body),
                color = SinopeColors.TextSecondary,
                fontSize = 12.sp,
            )
        },
        confirmButton = {
            DialogAction(
                text = stringResource(R.string.delete),
                onClick = onConfirm,
                filled = true,
            )
        },
        dismissButton = {
            DialogAction(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                filled = false,
            )
        },
    )
}

@Composable
private fun DialogAction(text: String, onClick: () -> Unit, filled: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (filled) SinopeColors.Danger else SinopeColors.InputBg)
            .border(
                width = 1.dp,
                color = if (filled) SinopeColors.Danger else SinopeColors.Border,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
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
