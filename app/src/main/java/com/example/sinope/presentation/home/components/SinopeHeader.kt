package com.example.sinope.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

@Composable
fun SinopeHeader(accountCount: Int, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = stringResource(R.string.brand_wordmark),
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    brush = SinopeColors.BrandGradient,
                ),
            )
            Text(
                text = stringResource(R.string.accounts_count, accountCount),
                color = SinopeColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HeaderIconButton(Icons.Outlined.Search, onClick = {})
            HeaderIconButton(Icons.Outlined.Person, onClick = onOpenSettings)
        }
    }
}

