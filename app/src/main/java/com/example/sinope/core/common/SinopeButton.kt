package com.example.sinope.core.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource


@Composable
fun SinopeButton(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: (() -> Unit)? = null,
    borderWidth: Dp? = null,
    borderColor: Color = SinopeColors.Border,
    roundedCornerSize: Dp = 14.dp,
    textColor: Color = SinopeColors.OnBrand,
    fontSize: TextUnit = 14.sp,
    padHor: Dp = 0.dp,
    padVer: Dp = 0.dp,
    contentPadHor: Dp = 0.dp,
    contentPadVer: Dp = 0.dp,
    tintColor:Color=Color.Black,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    fontWeight: FontWeight = FontWeight.Bold,
    backgroundColor: Brush = SinopeColors.ButtonGradient,
    icon: ImageVector? = null,
    iconSize: Dp = 16.dp,
    iconSpacing: Dp = 8.dp
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = padHor, vertical = padVer)
            .clip(RoundedCornerShape(roundedCornerSize))
            .border(
                borderWidth ?: 0.dp,
                borderColor,
                RoundedCornerShape(roundedCornerSize),
            )
            .background(brush = backgroundColor)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = contentPadHor, vertical = contentPadVer),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = verticalAlignment
    ) {
        icon?.let {
            Icon(it,
                null,
                tint = tintColor, modifier = Modifier.size(iconSize))
            Spacer(Modifier.width(iconSpacing))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
        )
    }
}

@Preview
@Composable
fun NewsTextButton(
) {
    SinopeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        text = "back",
        icon = Icons.Filled.Add,
        borderWidth = 1.dp,
        backgroundColor = SinopeColors.BrandGradient,
        textColor = Color.Black,
        onClick = {

        },
    )
//    AddAccountFab {  }
}


@Composable
private fun AddAccountFab(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(SinopeColors.BrandGradient)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Filled.Add, null, tint = Color.Black, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.add_account),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}