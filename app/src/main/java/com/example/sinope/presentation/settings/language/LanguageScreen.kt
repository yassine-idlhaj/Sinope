package com.example.sinope.presentation.language

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sinope.R
import com.example.sinope.core.common.BackBar
import com.example.sinope.core.common.RowDivider
import com.example.sinope.core.common.SectionLabel
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.model.AppLanguages
import com.example.sinope.presentation.model.LanguageOption
import com.example.sinope.presentation.settings.viewModel.LanguageViewModel

/**
 * One selectable language. [nativeName] is the language's own endonym, shown under the English
 * name so the row is readable to someone who can't yet read the app's current language.
 */


/** Languages offered in the picker, in the order they appear on screen. */



/**
 * Language screen: a single-choice list of the languages Sinope ships with. Presentation only —
 * the selection is a parameter and the tap is a callback, so the wiring lives outside this file.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    languages: List<LanguageOption> = AppLanguages,
    viewModel: LanguageViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {

    val languageViewModel by viewModel.language.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Log.d("TEST",languageViewModel.toString())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SinopeColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackBar(title = stringResource(R.string.language), onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
            ) {
                Spacer(Modifier.height(10.dp))

                SectionLabel(text = stringResource(R.string.app_language), color = SinopeColors.TextMuted)

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SinopeColors.Surface)
                        .border(1.dp, SinopeColors.Border, RoundedCornerShape(16.dp)),
                ) {
                    languages.forEachIndexed { index, language ->
                        if (index > 0) RowDivider()
                        LanguageRow(
                            language = language,
                            selected = languageViewModel == language.language,
                            onClick = { viewModel.selectLanguage(context, language.language) },
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.language_description),
                    color = SinopeColors.TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }
    }
}

/** Flag, name over endonym, and the single-choice indicator on the right. */
@Composable
private fun LanguageRow(
    language: LanguageOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) SinopeColors.Cyan.copy(alpha = 0.05f) else Color.Transparent,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(text = language.flag, fontSize = 22.sp)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(language.nameRes),
                color = SinopeColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = language.nativeName,
                color = SinopeColors.TextSecondary,
                fontSize = 10.sp,
            )
        }

        SelectionIndicator(selected = selected)
    }
}

/** Filled cyan tick when picked, hollow ring otherwise. */
@Composable
private fun SelectionIndicator(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (selected) SinopeColors.Cyan else Color.Transparent)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else SinopeColors.Border,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = SinopeColors.Background,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Preview(
    name = "Language",
    showBackground = true,
    backgroundColor = 0xFF0A0A0F,
    widthDp = 300,
    heightDp = 780,
)
@Composable
private fun LanguageScreenPreview() {
    LanguageScreen()
}
