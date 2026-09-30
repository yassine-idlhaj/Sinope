package com.example.sinope.core.utils

import java.text.Normalizer
import java.util.Locale

/**
 * Combining marks to strip, minus the Japanese voiced sound marks (U+3099/U+309A). Those are
 * technically accents too, but dropping them folds が into か, which would match words the user
 * never typed. Every other mark — French accents, Arabic harakat — is noise for a search box.
 */
private val AccentMarks = Regex("[\\p{Mn}&&[^\\u3099\\u309A]]+")

/**
 * Folds [this] to a form search can compare on: accents removed and lower-cased with
 * [Locale.ROOT], so "Français", "FRANCAIS" and "francais" all land on the same text.
 *
 * The root locale matters — Turkish lower-cases "I" to "ı", so a device locale would make the
 * query and the account name fold differently.
 */
fun String.foldForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(AccentMarks, "")
        .lowercase(Locale.ROOT)
