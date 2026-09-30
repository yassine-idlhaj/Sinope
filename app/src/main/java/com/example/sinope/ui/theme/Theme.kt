package com.example.sinope.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.sinope.core.utils.SinopeColors

/**
 * Sinope is a dark-only app: every screen paints itself from [SinopeColors].
 *
 * The Material scheme therefore has to be dark too, and derived from the same palette. It used to
 * follow the system theme with dynamic colour, which meant Material-owned surfaces — the bottom
 * sheet, dialogs, ripples — were light on a phone in light mode while the app's own content stayed
 * near-white. The long-press sheet rendered white text on a white sheet and was unreadable.
 *
 * Nothing here should follow the system or the wallpaper. One palette, always.
 */
private val SinopeColorScheme = darkColorScheme(
    primary = SinopeColors.Cyan,
    onPrimary = SinopeColors.Background,
    secondary = SinopeColors.Violet,
    onSecondary = SinopeColors.Background,
    tertiary = SinopeColors.Pink,
    onTertiary = SinopeColors.Background,

    background = SinopeColors.Background,
    onBackground = SinopeColors.TextPrimary,

    // ModalBottomSheet and friends pull their container from the surfaceContainer family
    // depending on the Material3 version, so every step maps to the vault's own surfaces.
    surface = SinopeColors.Surface,
    onSurface = SinopeColors.TextPrimary,
    surfaceVariant = SinopeColors.InputBg,
    onSurfaceVariant = SinopeColors.TextSecondary,
    surfaceContainerLowest = SinopeColors.Background,
    surfaceContainerLow = SinopeColors.SurfaceAlt,
    surfaceContainer = SinopeColors.Surface,
    surfaceContainerHigh = SinopeColors.Surface,
    surfaceContainerHighest = SinopeColors.InputBg,
    inverseSurface = SinopeColors.TextPrimary,
    inverseOnSurface = SinopeColors.Background,

    outline = SinopeColors.Border,
    outlineVariant = SinopeColors.Border,

    error = SinopeColors.Danger,
    onError = SinopeColors.Background,
    errorContainer = SinopeColors.Danger,
    onErrorContainer = SinopeColors.TextPrimary,
)

@Composable
fun SinopeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SinopeColorScheme,
        typography = Typography,
        content = content,
    )
}
