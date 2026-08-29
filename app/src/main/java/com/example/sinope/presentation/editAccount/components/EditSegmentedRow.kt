package com.example.sinope.presentation.editAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.common.RowDivider
import com.example.sinope.core.utils.SinopeColors

/**
 * Labelled segmented control ("Digits  [6][8]"). The selectable variant of the read-only row on
 * the manual-add tab — each option reports its own value back through [onSelect].
 */
@Composable
internal fun <T> EditSegmentedRow(
    label: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    accent: Color = SinopeColors.Cyan,
    showDivider: Boolean = false,
    optionLabel: (T) -> String = { it.toString() },
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = SinopeColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SinopeColors.Track)
                .padding(2.dp),
        ) {
            options.forEach { option ->
                val active = option == selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) accent else Color.Transparent)
                        .clickable { onSelect(option) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = optionLabel(option),
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
