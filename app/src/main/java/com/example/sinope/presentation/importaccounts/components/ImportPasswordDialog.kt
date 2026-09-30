package com.example.sinope.presentation.importaccounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.R
import com.example.sinope.core.common.VaultTextField
import com.example.sinope.core.utils.SinopeColors

/** Asks for the password of the .sinope file the user just picked. */
@Composable
internal fun ImportPasswordDialog(
    wrongPassword: Boolean,
    onConfirm: (password: String) -> Unit,
    onDismiss: () -> Unit,
) {
    // Plain remember: saveable state can reach the disk, and this is a password.
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SinopeColors.Surface,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SinopeColors.Amber.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.SaveAlt, null, tint = SinopeColors.Amber, modifier = Modifier.size(18.dp))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.import_dialog_title),
                color = SinopeColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.import_dialog_body),
                    color = SinopeColors.TextSecondary,
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(14.dp))
                VaultTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = stringResource(R.string.backup_password),
                    icon = Icons.Outlined.Key,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                )
                if (wrongPassword) {
                    Text(
                        text = stringResource(R.string.import_wrong_password),
                        color = SinopeColors.Danger,
                        fontSize = 11.sp,
                    )
                }
            }
        },
        confirmButton = {
            DialogAction(
                text = stringResource(R.string.import_open),
                onClick = { onConfirm(password) },
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
            .background(if (filled) SinopeColors.Amber else SinopeColors.InputBg)
            .border(
                width = 1.dp,
                color = if (filled) SinopeColors.Amber else SinopeColors.Border,
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
