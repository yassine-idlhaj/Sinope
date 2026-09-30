package com.example.sinope.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sinope.core.utils.SinopeColors

/**
 * Square header action. [active] lights it up in the brand cyan, so a toggled mode such as
 * search reads as on without needing a second icon.
 */
@Composable
fun HeaderIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean = false,
    contentDescription: String? = null,
) {
    val background by animateColorAsState(
        targetValue = if (active) SinopeColors.Cyan.copy(alpha = 0.14f) else SinopeColors.InputBg,
        label = "headerIconBackground",
    )
    val border by animateColorAsState(
        targetValue = if (active) SinopeColors.Cyan.copy(alpha = 0.5f) else SinopeColors.Border,
        label = "headerIconBorder",
    )
    val tint by animateColorAsState(
        targetValue = if (active) SinopeColors.Cyan else SinopeColors.TextSecondary,
        label = "headerIconTint",
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(16.dp))
    }
}
