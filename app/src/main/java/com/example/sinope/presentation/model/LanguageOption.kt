package com.example.sinope.presentation.model

import androidx.annotation.StringRes
import com.example.sinope.R
import com.example.sinope.domain.model.AppLanguage

data class LanguageOption(
    val language: AppLanguage,
    val tag: String,
    val flag: String,
    @param:StringRes val nameRes: Int,
    /**
     * The language's own endonym, shown under the name. Left blank for rows whose subtitle is not
     * a language name and therefore has to be translated — see [subtitleRes].
     */
    val nativeName: String = "",
    /** Translated subtitle, used instead of [nativeName] when set. */
    @param:StringRes val subtitleRes: Int? = null,
)

val AppLanguages: List<LanguageOption> = listOf(
    // First, and the default: AppLanguage.SYSTEM is what's stored until the user picks something,
    // so without this row nothing would appear selected and "follow the device" would be a state
    // you could leave but never return to.
    LanguageOption(
        language = AppLanguage.SYSTEM,
        tag = "",
        flag = "🌐",
        nameRes = R.string.language_system,
        subtitleRes = R.string.language_system_subtitle,
    ),
    LanguageOption(
        language = AppLanguage.ENGLISH,
        tag = "en",
        flag = "🇬🇧",
        nameRes = R.string.language_english,
        nativeName = "English",
    ),
    LanguageOption(
        language = AppLanguage.ARABIC,
        tag = "ar",
        flag = "🇲🇦",
        nameRes = R.string.language_arabic,
        nativeName = "العربية",
    ),
    LanguageOption(
        language = AppLanguage.FRENCH,
        tag = "fr",
        flag = "🇫🇷",
        nameRes = R.string.language_french,
        nativeName = "Français",
    ),
    LanguageOption(
        language = AppLanguage.SPANISH,
        tag = "es",
        flag = "🇪🇸",
        nameRes = R.string.language_spanish,
        nativeName = "Español",
    ),
    LanguageOption(
        language = AppLanguage.GERMAN,
        tag = "de",
        flag = "🇩🇪",
        nameRes = R.string.language_german,
        nativeName = "Deutsch",
    ),
    LanguageOption(
        language = AppLanguage.PORTUGUESE,
        tag = "pt",
        flag = "🇵🇹",
        nameRes = R.string.language_portuguese,
        nativeName = "Português",
    ),
    LanguageOption(
        language = AppLanguage.TURKISH,
        tag = "tr",
        flag = "🇹🇷",
        nameRes = R.string.language_turkish,
        nativeName = "Türkçe",
    ),
    LanguageOption(
        language = AppLanguage.JAPANESE,
        tag = "ja",
        flag = "🇯🇵",
        nameRes = R.string.language_japanese,
        nativeName = "日本語",
    ),
)