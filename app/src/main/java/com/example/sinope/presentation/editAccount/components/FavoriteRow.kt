package com.example.sinope.presentation.editAccount.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/** Row that pins the account to the vault's Favorites section, with a small sliding switch. */
@Composable
internal fun FavoriteRow(
    favorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SinopeColors.InputBg)
            .border(1.dp, SinopeColors.Border, RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = if (favorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
            contentDescription = null,
            tint = if (favorite) SinopeColors.Amber else SinopeColors.TextMuted,
            modifier = Modifier.size(16.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.favorite),
                color = SinopeColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.favorite_description),
                color = SinopeColors.TextMuted,
                fontSize = 10.sp,
            )
        }
        SinopeSwitch(checked = favorite, accent = SinopeColors.Amber)
    }
}

/** Compact on/off track. Not clickable itself — the whole [FavoriteRow] is the hit target. */
@Composable
private fun SinopeSwitch(checked: Boolean, accent: Color) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 16.dp else 2.dp,
        animationSpec = tween(180),
        label = "switchThumb",
    )

    Box(
        modifier = Modifier
            .width(36.dp)
            .height(20.dp)
            .clip(CircleShape)
            .background(if (checked) accent.copy(alpha = 0.35f) else SinopeColors.Track)
            .border(
                width = 1.dp,
                color = if (checked) accent.copy(alpha = 0.6f) else SinopeColors.Border,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(16.dp)
                .clip(CircleShape)
                .background(if (checked) accent else SinopeColors.TextMuted),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F, widthDp = 300)
@Composable
private fun FavoriteRowPreview() {
    Column(
        Modifier.background(SinopeColors.Background).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FavoriteRow(favorite = true, onToggle = {})
        FavoriteRow(favorite = false, onToggle = {})
    }
}
