package com.example.sinope.testing

import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory stand-in for the DataStore-backed preferences.
 *
 * Hand-written rather than mocked: the interface has two methods, and a fake that actually stores
 * the flag lets a test assert on *behaviour* ("reading after a save yields true") instead of on
 * interaction bookkeeping alone. The call counters are still exposed because the use-case and
 * view-model tests need to prove delegation happens exactly once.
 */
class FakeLocalUserPreferences(initialAppEntry: Boolean = false) : ILocalUserPreferences {

    private val appEntry = MutableStateFlow(initialAppEntry)

    var saveAppEntryCallCount = 0
        private set

    var readAppEntryCallCount = 0
        private set

    override suspend fun saveAppEntry() {
        saveAppEntryCallCount++
        appEntry.value = true
    }

    override fun readAppEntry(): Flow<Boolean> {
        readAppEntryCallCount++
        return appEntry
    }

    // --- Settings the app_entry tests don't exercise, kept in memory so the fake stays usable. ---

    private val language = MutableStateFlow("")
    private val biometricLock = MutableStateFlow(false)
    private val screenshotProtection = MutableStateFlow(false)

    override suspend fun saveLanguage(language: String) {
        this.language.value = language
    }

    override fun readLanguage(): Flow<String> = language

    override suspend fun saveBiometricLock(biometricLock: Boolean) {
        this.biometricLock.value = biometricLock
    }

    override fun readBiometricLock(): Flow<Boolean> = biometricLock

    override suspend fun saveScreenshotProtection(screenshotProtection: Boolean) {
        this.screenshotProtection.value = screenshotProtection
    }

    override fun readScreenshotProtection(): Flow<Boolean> = screenshotProtection

    /** Pushes a new value to collectors of [readAppEntry], simulating an out-of-band write. */
    fun setAppEntry(value: Boolean) {
        appEntry.value = value
    }
}
