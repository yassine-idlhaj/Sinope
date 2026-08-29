package com.example.sinope.presentation.editAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors

/** Emoji offered as account avatars — the same glyph set the vault cards render. */
internal val emojiChoices = listOf(
    "🔐", "🐱", "🔍", "🪟", "🪙", "💬", "📧", "🛒", "🎮", "☁️", "🏦", "🎵", "📦", "🔑",
)

/** Horizontally scrolling row of emoji tiles; the selected one gets an accent ring. */
@Composable
internal fun EmojiPicker(
    selected: String,
    accent: Color,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp),
    ) {
        items(emojiChoices) { emoji ->
            val isSelected = emoji == selected
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) accent.copy(alpha = 0.14f) else SinopeColors.InputBg
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) accent else SinopeColors.Border,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable { onSelect(emoji) },
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F, widthDp = 300)
@Composable
private fun EmojiPickerPreview() {
    Box(Modifier.background(SinopeColors.Background).padding(12.dp)) {
        EmojiPicker(selected = "🐱", accent = SinopeColors.Cyan, onSelect = {})
    }
}
