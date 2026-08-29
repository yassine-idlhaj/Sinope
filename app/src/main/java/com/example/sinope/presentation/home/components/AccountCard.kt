package com.example.sinope.presentation.home.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.model.AccountUi
import com.example.sinope.R
import androidx.compose.ui.res.stringResource


@Composable
fun AccountCard(
    account: AccountUi,
    onCopied:()-> Unit = {},
    onFavoriteClick:()-> Unit = {},
    onClick:()-> Unit = {},
    onLongClick:()-> Unit = {},
) {
    // Accent shifts to amber (≤10s) then red (≤5s) as the code nears expiry.
    val targetAccent = when {
        account.secondsLeft <= 5 -> SinopeColors.Danger
        account.secondsLeft <= 10 -> SinopeColors.Amber
        else -> account.color
    }
    val accent by animateColorAsState(targetAccent, tween(400), label = "accent")
    val progress by animateFloatAsState(
        targetValue = account.secondsLeft / account.period.toFloat(),
        animationSpec = tween(900, easing = LinearEasing),
        label = "timer",
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SinopeColors.Surface)
            .border(1.dp, SinopeColors.Border, RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(account.color.copy(alpha = 0.12f))
                    .border(1.5.dp, account.color.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(account.emoji, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.issuer,
                    color = SinopeColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = account.email,
                    color = SinopeColors.TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = if (account.favorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (account.favorite) SinopeColors.Amber else SinopeColors.TextMuted,
                modifier = Modifier.size(18.dp)
                    .clickable{
                        onFavoriteClick()
                    },
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = codeStyle(account.code),
                style = TextStyle(
                    color = accent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 5.sp,
                    fontFamily = FontFamily.Monospace,
                ),
            )
            Icon(
                Icons.Outlined.ContentCopy,
                contentDescription = stringResource(R.string.copy_code),
                tint = SinopeColors.TextMuted,
                modifier = Modifier.size(18.dp)
                    .clickable{
                        toClipBoard(account.code,context)
                        onCopied()
                    }
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SinopeColors.Track),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(3.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(accent),
                )
            }
            Text(
                text = stringResource(R.string.seconds_left, account.secondsLeft),
                color = accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}


private fun toClipBoard(code:String,context: Context) {
    val clipBoard = context.getSystemService(ClipboardManager::class.java)

    clipBoard.setPrimaryClip(
        ClipData.newPlainText(context.getString(R.string.totp_code_clip_label), code)
    )
}

private fun codeStyle(code:String):String {
    /// "523891",
    val styledCode = code.toMutableList()
    styledCode.add(3,' ')
    return styledCode.joinToString("")
}