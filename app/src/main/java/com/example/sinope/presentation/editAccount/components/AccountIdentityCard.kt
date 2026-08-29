package com.example.sinope.presentation.editAccount.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/**
 * Live preview of how the account will look in the vault: the picked emoji and accent applied to
 * the same avatar the home cards use, so edits are visible before they are saved.
 */
@Composable
internal fun AccountIdentityCard(
    emoji: String,
    issuer: String,
    accountName: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val animatedAccent by animateColorAsState(accent, tween(300), label = "identityAccent")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(animatedAccent.copy(alpha = 0.10f), SinopeColors.Surface),
                )
            )
            .border(1.dp, animatedAccent.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(animatedAccent.copy(alpha = 0.12f))
                .border(1.5.dp, animatedAccent.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = 22.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = issuer.ifBlank { stringResource(R.string.untitled) },
                color = SinopeColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = accountName.ifBlank { stringResource(R.string.no_account_name) },
                color = SinopeColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F, widthDp = 300)
@Composable
private fun AccountIdentityCardPreview() {
    Box(Modifier.background(SinopeColors.Background).padding(12.dp)) {
        AccountIdentityCard(
            emoji = "🐱",
            issuer = "GitHub",
            accountName = "dev@example.com",
            accent = SinopeColors.Cyan,
        )
    }
}
