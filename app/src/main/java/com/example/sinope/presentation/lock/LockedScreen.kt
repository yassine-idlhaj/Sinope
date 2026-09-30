package com.example.sinope.presentation.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.R
import com.example.sinope.core.common.SinopeButton
import com.example.sinope.core.utils.SinopeColors

/**
 * What the app shows while the biometric lock is on and the vault is still locked.
 *
 * It exists so a cancelled or failed prompt has somewhere to land: without it the activity draws
 * nothing at all, and the only way out is force-stopping the app.
 */
@Composable
fun LockedScreen(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SinopeColors.Background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SinopeColors.Cyan.copy(alpha = 0.10f))
                .border(1.dp, SinopeColors.Cyan.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = SinopeColors.Cyan,
                modifier = Modifier.size(26.dp),
            )
        }

        Text(
            text = stringResource(R.string.brand_wordmark),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                brush = SinopeColors.BrandGradient,
            ),
            modifier = Modifier.padding(top = 24.dp),
        )

        Text(
            text = stringResource(R.string.locked_title),
            color = SinopeColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp),
        )

        Text(
            // The specific reason when there is one — "no biometric is enrolled" needs saying,
            // because the fix for it lives in system settings rather than here.
            text = error ?: stringResource(R.string.locked_subtitle),
            color = if (error != null) SinopeColors.Danger else SinopeColors.TextMuted,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )

        SinopeButton(
            text = stringResource(R.string.unlock),
            onClick = onUnlock,
            contentPadVer = 15.dp,
            roundedCornerSize = 16.dp,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F, widthDp = 300, heightDp = 620)
@Composable
private fun LockedScreenPreview() {
    LockedScreen(onUnlock = {})
}
