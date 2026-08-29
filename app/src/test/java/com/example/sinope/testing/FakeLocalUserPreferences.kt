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

    /** Pushes a new value to collectors of [readAppEntry], simulating an out-of-band write. */
    fun setAppEntry(value: Boolean) {
        appEntry.value = value
    }
}
