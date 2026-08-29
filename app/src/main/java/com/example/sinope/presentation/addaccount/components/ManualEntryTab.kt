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
import com.example.sinope.core.common.RowDivider
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

@Composable
fun ManualEntryTab(onSave: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        InputLabel(stringResource(R.string.account_info))
        FakeInput(Icons.Outlined.Shield, "GitHub")
        FakeInput(Icons.Outlined.Person, "dev@example.com")
        FakeInput(Icons.Outlined.VpnKey, "JBSWY3DPEHPK3PXP")

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
            SegmentedRow(stringResource(R.string.digits), listOf("6", "8"), selectedIndex = 0, showDivider = true)
            SegmentedRow(stringResource(R.string.period), listOf("30s", "60s"), selectedIndex = 0, showDivider = false)
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
private fun FakeInput(icon: ImageVector, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SinopeColors.InputBg)
            .border(1.dp, SinopeColors.Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, null, tint = SinopeColors.TextMuted, modifier = Modifier.size(14.dp))
        Text(
            value,
            color = SinopeColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SegmentedRow(
    label: String,
    options: List<String>,
    selectedIndex: Int,
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
