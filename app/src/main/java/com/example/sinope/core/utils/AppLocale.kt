package com.example.sinope.core.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sinope.data.app_manager.dataStore
import com.example.sinope.domain.model.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

/** BCP-47 tag for [this] language; empty means "follow the system". */
fun AppLanguage.toLanguageTag(): String = when (this) {
    AppLanguage.SYSTEM -> ""

    AppLanguage.ENGLISH -> "en"
    AppLanguage.ARABIC -> "ar"
    AppLanguage.FRENCH -> "fr"
    AppLanguage.SPANISH -> "es"
    AppLanguage.GERMAN -> "de"
    AppLanguage.PORTUGUESE -> "pt"
    AppLanguage.TURKISH -> "tr"
    AppLanguage.JAPANESE -> "ja"
}

/**
 * Applies [language] to the app.
 *
 * Two paths, because per-app languages only became a platform feature in Android 13:
 *
 * - API 33+ hands the choice to the framework's [LocaleManager], which persists it itself and
 *   surfaces it in the system's per-app language settings.
 * - API 30–32 has no such API, so the locale is re-read from DataStore in
 *   [MainActivity.attachBaseContext][android.content.ContextWrapper.attachBaseContext] via
 *   [withPersistedAppLocale] and the visible activity is recreated to pick it up.
 *
 * The caller is expected to have persisted [language] already — on API < 33 the recreated activity
 * reads it back from DataStore, not from this argument.
 */
fun applyLanguages(
    context: Context,
    language: AppLanguage,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(language.toLanguageTag())
    } else {
        context.findActivity()?.recreate()
    }
}

/**
 * Returns [this] context re-based on the language saved in DataStore, or [this] unchanged when the
 * user follows the system. Only meaningful below API 33 — from Android 13 up the framework has
 * already applied the per-app locale by the time a context is handed out.
 *
 * Call from `attachBaseContext`, which runs before the activity inflates anything, so every
 * `stringResource` in the tree resolves against the chosen locale.
 */
fun Context.withPersistedAppLocale(): Context {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return this

    val tag = readPersistedLanguage().toLanguageTag()
    if (tag.isEmpty()) return this

    val locale = Locale.forLanguageTag(tag)
    Locale.setDefault(locale)

    val configuration = Configuration(resources.configuration).apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    return createConfigurationContext(configuration)
}

/**
 * Reads the saved language straight off DataStore. Blocking on purpose: `attachBaseContext` cannot
 * suspend, and the locale has to be known before any resource is resolved. It is a single small
 * preferences read, and it only happens on API < 33.
 */
private fun Context.readPersistedLanguage(): AppLanguage = runCatching {
    runBlocking {
        val stored = dataStore.data.first()[stringPreferencesKey(Constants.LANGUAGE)]
        AppLanguage.valueOf(stored ?: AppLanguage.SYSTEM.name)
    }
}.getOrDefault(AppLanguage.SYSTEM)

/** Walks the [ContextWrapper] chain looking for the hosting activity. */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
