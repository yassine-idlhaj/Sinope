package com.example.sinope.core.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/**
 * Accent used by a [SinopeSnackbarHost] message. Drives the leading icon and the accent colour of
 * the pill, so a "Code copied" reads differently from a failed save at a glance.
 */
enum class SinopeSnackbarTone(internal val accent: Color, internal val icon: ImageVector) {
    Info(SinopeColors.Cyan, Icons.Outlined.Info),
    Success(SinopeColors.Green, Icons.Outlined.TaskAlt),
    Error(SinopeColors.Danger, Icons.Outlined.ErrorOutline),
}

/** [SnackbarVisuals] carrying a [SinopeSnackbarTone] on top of the Material defaults. */
data class SinopeSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val tone: SinopeSnackbarTone = SinopeSnackbarTone.Info,
) : SnackbarVisuals

/**
 * Shows a themed message on this host. Same contract as [SnackbarHostState.showSnackbar] — it
 * suspends until the snackbar is dismissed and returns how that happened.
 */
suspend fun SnackbarHostState.showSinopeSnackbar(
    message: String,
    tone: SinopeSnackbarTone = SinopeSnackbarTone.Info,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult = showSnackbar(
    SinopeSnackbarVisuals(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration,
        tone = tone,
    )
)

/**
 * Drop-in replacement for [SnackbarHost] that renders the vault's dark pill instead of the Material
 * default. Messages sent as plain strings fall back to [SinopeSnackbarTone.Info].
 */
@Composable
fun SinopeSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        SinopeSnackbar(data = data)
    }
}

/** The pill itself: accent dot, message, optional action and dismiss affordances. */
@Composable
fun SinopeSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val visuals = data.visuals
    val tone = (visuals as? SinopeSnackbarVisuals)?.tone ?: SinopeSnackbarTone.Info

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SinopeColors.Surface)
            .border(1.dp, tone.accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(tone.accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tone.icon, null, tint = tone.accent, modifier = Modifier.size(14.dp))
        }

        Text(
            text = visuals.message,
            color = SinopeColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )

        visuals.actionLabel?.let { label ->
            Text(
                text = label,
                color = tone.accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.clickable { data.performAction() },
            )
        }

        if (visuals.withDismissAction) {
            if (visuals.actionLabel != null) Box(Modifier.width(2.dp))
            Icon(
                Icons.Outlined.Close,
                contentDescription = stringResource(R.string.dismiss),
                tint = SinopeColors.TextMuted,
                modifier = Modifier
                    .size(15.dp)
                    .clickable { data.dismiss() },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F, widthDp = 300)
@Composable
private fun SinopeSnackbarPreview() {
    Box(Modifier.background(SinopeColors.Background)) {
        SinopeSnackbar(data = previewSnackbarData("Code copied", SinopeSnackbarTone.Info, "UNDO"))
    }
}

private fun previewSnackbarData(
    message: String,
    tone: SinopeSnackbarTone,
    actionLabel: String?,
): SnackbarData = object : SnackbarData {
    override val visuals = SinopeSnackbarVisuals(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = true,
        tone = tone,
    )

    override fun performAction() = Unit
    override fun dismiss() = Unit
}
