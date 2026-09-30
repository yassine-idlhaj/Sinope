package com.example.sinope.presentation.addaccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import com.example.sinope.core.common.RowDivider
import com.example.sinope.core.common.VaultTextField
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/**
 * Manual "enter a secret by hand" form. Stateless: every value comes in as a parameter and every
 * change goes out as a callback, so the ViewModel owns the form state.
 */
@Composable
fun ManualEntryTab(
    issuer: String,
    accountName: String,
    secret: String,
    digits: Int,
    period: Int,
    errorMessage: String?,
    onIssuerChange: (String) -> Unit,
    onAccountNameChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onDigitsChange: (Int) -> Unit,
    onPeriodChange: (Int) -> Unit,
    onSave: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        InputLabel(stringResource(R.string.account_info))
        VaultTextField(
            value = issuer,
            onValueChange = onIssuerChange,
            placeholder = stringResource(R.string.hint_issuer),
            icon = Icons.Outlined.Shield,
        )
        VaultTextField(
            value = accountName,
            onValueChange = onAccountNameChange,
            placeholder = stringResource(R.string.hint_account_name),
            icon = Icons.Outlined.Person,
        )
        VaultTextField(
            value = secret,
            onValueChange = onSecretChange,
            placeholder = stringResource(R.string.hint_secret),
            icon = Icons.Outlined.VpnKey,
            // Password keyboard: no autocorrect or suggestions on a Base32 key.
            keyboardType = KeyboardType.Password,
        )

        errorMessage?.let {
            Text(
                text = it,
                color = SinopeColors.Danger,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        Spacer(Modifier.height(14.dp))
        InputLabel(stringResource(R.string.advanced))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SinopeColors.InputBg)
                .border(1.dp, SinopeColors.Border, RoundedCornerShape(16.dp)),
        ) {
            SegmentedRow(
                label = stringResource(R.string.digits),
                options = listOf("6", "8"),
                selectedIndex = if (digits == 8) 1 else 0,
                onSelect = { onDigitsChange(if (it == 1) 8 else 6) },
                showDivider = true,
            )
            SegmentedRow(
                label = stringResource(R.string.period),
                options = listOf("30s", "60s"),
                selectedIndex = if (period == 60) 1 else 0,
                onSelect = { onPeriodChange(if (it == 1) 60 else 30) },
                showDivider = false,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            SinopeColors.Cyan,
                            SinopeColors.VioletDeep
                        )
                    )
                )
                .clickable(onClick = onSave)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(R.string.add_to_vault),
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


@Composable
private fun InputLabel(text: String) {
    Text(
        text = text,
        color = SinopeColors.TextMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun SegmentedRow(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    showDivider: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            color = SinopeColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SinopeColors.Track)
                .padding(2.dp),
        ) {
            options.forEachIndexed { index, option ->
                val active = index == selectedIndex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) SinopeColors.Cyan else Color.Transparent)
                        .clickable { onSelect(index) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        option,
                        color = if (active) Color.Black else SinopeColors.TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
    if (showDivider) RowDivider()
}
