package com.example.sinope.presentation.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.common.BackBar
import com.example.sinope.core.common.RowDivider
import com.example.sinope.core.common.SectionLabel
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/**
 * Settings screen: vault stats at a glance, the two security switches, backup import/export and
 * the destructive wipe. Presentation only — every value is a parameter and every tap is a
 * callback, so the wiring lives outside this file.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    accountCount: Int = 0,
    favoriteCount: Int = 0,
    biometricLockEnabled: Boolean = false,
    screenshotProtectionEnabled: Boolean = false,
    onBack: () -> Unit = {},
    onToggleBiometricLock: () -> Unit = {},
    onToggleScreenshotProtection: () -> Unit = {},
    onExportAccounts: () -> Unit = {},
    onImportAccounts: () -> Unit = {},
    onLanguageChange: () -> Unit = {},
    onDeleteAllAccounts: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SinopeColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackBar(title = stringResource(R.string.settings), onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
            ) {
                Spacer(Modifier.height(6.dp))

                VaultStatsCard(
                    accountCount = accountCount,
                    favoriteCount = favoriteCount,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(14.dp))

                SectionLabel(text = stringResource(R.string.section_language), color = SinopeColors.TextMuted)
                SettingsGroup {
                    SettingsNavRow(
                        icon = Icons.Outlined.Translate,
                        accent = SinopeColors.Cyan,
                        title = stringResource(R.string.language),
                        subtitle = stringResource(R.string.require_on_launch),
                        onClick = onLanguageChange,
                    )
                }

                Spacer(Modifier.height(14.dp))

                SectionLabel(text = stringResource(R.string.section_security), color = SinopeColors.TextMuted)
                SettingsGroup {
                    SettingsToggleRow(
                        icon = Icons.Outlined.Fingerprint,
                        accent = SinopeColors.Cyan,
                        title = stringResource(R.string.biometric_lock),
                        subtitle = stringResource(R.string.require_on_launch),
                        checked = biometricLockEnabled,
                        onToggle = onToggleBiometricLock,
                    )
                    RowDivider()
                    SettingsToggleRow(
                        icon = Icons.Outlined.Lock,
                        accent = SinopeColors.Danger,
                        title = stringResource(R.string.screenshot_protection),
                        subtitle = stringResource(R.string.prevent_capture),
                        checked = screenshotProtectionEnabled,
                        onToggle = onToggleScreenshotProtection,
                    )
                }

                Spacer(Modifier.height(10.dp))

                SectionLabel(text = stringResource(R.string.section_backup), color = SinopeColors.TextMuted)
                SettingsGroup {
                    SettingsNavRow(
                        icon = Icons.Outlined.Upload,
                        accent = SinopeColors.Green,
                        title = stringResource(R.string.export_accounts),
                        subtitle = stringResource(R.string.encrypted_backup),
                        onClick = onExportAccounts,
                    )
                    RowDivider()
                    SettingsNavRow(
                        icon = Icons.Outlined.SaveAlt,
                        accent = SinopeColors.Amber,
                        title = stringResource(R.string.import_accounts),
                        subtitle = stringResource(R.string.restore_from_backup),
                        onClick = onImportAccounts,
                    )
                }

                Spacer(Modifier.height(10.dp))

                SectionLabel(text = stringResource(R.string.section_danger_zone), color = SinopeColors.TextMuted)
                SettingsGroup(
                    background = SinopeColors.Danger.copy(alpha = 0.06f),
                    borderColor = SinopeColors.Danger.copy(alpha = 0.35f),
                ) {
                    SettingsNavRow(
                        icon = Icons.Outlined.DeleteOutline,
                        accent = SinopeColors.Danger,
                        title = stringResource(R.string.delete_all_accounts),
                        subtitle = stringResource(R.string.permanently_removes_all_data),
                        titleColor = SinopeColors.Danger,
                        chevronColor = SinopeColors.Danger,
                        onClick = onDeleteAllAccounts,
                    )
                }
            }
        }
    }
}

/**
 * Three-up summary of the vault: how many accounts it holds, how many are pinned, and a lock
 * badge standing in for "everything here is encrypted".
 */
@Composable
private fun VaultStatsCard(
    accountCount: Int,
    favoriteCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SinopeColors.Surface)
            .border(1.dp, SinopeColors.Border, RoundedCornerShape(18.dp))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatCell(
            value = accountCount.toString(),
            valueBrush = SinopeColors.BrandGradient,
            label = stringResource(R.string.stat_accounts),
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        StatCell(
            value = favoriteCount.toString(),
            valueBrush = SolidColor(SinopeColors.Amber),
            label = stringResource(R.string.stat_favorites),
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        StatCell(
            value = "🔒",
            valueBrush = SolidColor(SinopeColors.Amber),
            label = stringResource(R.string.stat_security),
            modifier = Modifier.weight(1f),
        )
    }
}

/** One column of [VaultStatsCard]: a big brand-tinted value over a small grey caption. */
@Composable
private fun StatCell(
    value: String,
    valueBrush: Brush,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                brush = valueBrush,
            ),
        )
        Text(
            text = label,
            color = SinopeColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/** Hairline separating two [StatCell]s. */
@Composable
private fun StatDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(34.dp)
            .background(SinopeColors.Border),
    )
}

/** Rounded card that stacks settings rows, with [RowDivider]s supplied by the caller. */
@Composable
private fun SettingsGroup(
    modifier: Modifier = Modifier,
    background: Color = SinopeColors.Surface,
    borderColor: Color = SinopeColors.Border,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
    ) {
        content()
    }
}

/** Settings row ending in a switch; the whole row is the hit target. */
@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    SettingsRow(
        icon = icon,
        accent = accent,
        title = title,
        subtitle = subtitle,
        onClick = onToggle,
    ) {
        SinopeSwitch(checked = checked)
    }
}

/** Settings row ending in a chevron, for anything that opens a flow of its own. */
@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = SinopeColors.TextPrimary,
    chevronColor: Color = SinopeColors.TextMuted,
) {
    SettingsRow(
        icon = icon,
        accent = accent,
        title = title,
        subtitle = subtitle,
        titleColor = titleColor,
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = chevronColor,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** Shared skeleton: tinted icon tile, title over subtitle, and a caller-supplied trailing slot. */
@Composable
private fun SettingsRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = SinopeColors.TextPrimary,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconTile(icon = icon, accent = accent)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = SinopeColors.TextSecondary,
                fontSize = 10.sp,
            )
        }
        trailing()
    }
}

/** Rounded square holding a settings icon, tinted with that row's accent. */
@Composable
private fun IconTile(icon: ImageVector, accent: Color) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(17.dp),
        )
    }
}

/** On/off track. Not clickable itself — the surrounding row is the hit target. */
@Composable
private fun SinopeSwitch(checked: Boolean) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 3.dp,
        animationSpec = tween(180),
        label = "settingsSwitchThumb",
    )

    Box(
        modifier = Modifier
            .width(41.dp)
            .height(23.dp)
            .clip(CircleShape)
            .background(if (checked) SinopeColors.Cyan else SinopeColors.Track)
            .border(
                width = 1.dp,
                color = if (checked) SinopeColors.Cyan else SinopeColors.Border,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(17.dp)
                .clip(CircleShape)
                .background(if (checked) SinopeColors.TextPrimary else SinopeColors.TextMuted),
        )
    }
}

@Preview(
    name = "Settings",
    showBackground = true,
    backgroundColor = 0xFF0A0A0F,
    widthDp = 300,
    heightDp = 780,
)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        accountCount = 4,
        favoriteCount = 1,
        biometricLockEnabled = false,
        screenshotProtectionEnabled = true,
    )
}
