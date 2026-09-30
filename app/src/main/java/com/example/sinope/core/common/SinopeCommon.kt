package com.example.sinope.core.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors

/** Small uppercase section heading with an optional leading icon (e.g. "FAVORITES"). */
@Composable
internal fun SectionLabel(
    text: String,
    color: Color,
    leading: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        leading?.invoke()
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
        )
    }
}

/** Back row with the "‹" chevron and a title, shared by the Add and Settings screens. */
@Composable
internal fun BackBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "‹",
            color = SinopeColors.TextSecondary,
            fontSize = 32.sp,
            modifier = Modifier.clickable(onClick = onBack),
        )
        Text(title, color = SinopeColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/** 1dp hairline used to separate stacked rows inside grouped cards. */
@Composable
internal fun RowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SinopeColors.Border),
    )
}

/** Small uppercase field label sitting above an input (e.g. "ACCOUNT NAME"). */
@Composable
internal fun FieldLabel(text: String) {
    Text(
        text = text,
        color = SinopeColors.TextMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(bottom = 6.dp, top = 2.dp),
    )
}

/**
 * Editable text field styled like the vault's dark inputs: leading icon, rounded surface, and a
 * cyan caret. A real, focusable [BasicTextField] — used by the manual-add and edit screens.
 */
@Composable
internal fun VaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SinopeColors.InputBg)
            .border(1.dp, SinopeColors.Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(icon, null, tint = SinopeColors.TextMuted, modifier = Modifier.size(15.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(placeholder, color = SinopeColors.TextMuted, fontSize = 12.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = TextStyle(
                    color = SinopeColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
                cursorBrush = SolidColor(SinopeColors.Cyan),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Full-width primary action button with the brand gradient (e.g. "Save Changes"). */
@Composable
internal fun VaultPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(SinopeColors.Cyan, SinopeColors.VioletDeep)))
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text, color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** Palette of accent colors offered when picking an account's color. */
internal val accentChoices = listOf(
    SinopeColors.Cyan,
    SinopeColors.Violet,
    SinopeColors.Pink,
    SinopeColors.Green,
    SinopeColors.Amber,
    SinopeColors.VioletDeep,
    SinopeColors.OnBrand
)

/** Horizontal row of tappable color swatches; the selected one gets a light ring. */
@Composable
internal fun AccentPicker(
    selected: Color,
    onSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        accentChoices.forEach { color ->
            val isSelected = color == selected
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.9f))
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) SinopeColors.TextPrimary else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clickable { onSelect(color) },
            )
        }
    }
}
