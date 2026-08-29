package com.example.sinope.domain.usecases.app_entry

import com.example.sinope.testing.FakeLocalUserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [SaveAppEntry] is the write half of the onboarding-completion flag. It is a thin delegation, so
 * the contract worth pinning down is exactly that: it forwards to the preferences layer, once, and
 * adds no logic of its own that could silently swallow the write.
 */
class SaveAppEntryTest {

    private val preferences = FakeLocalUserPreferences()
    private val saveAppEntry = SaveAppEntry(preferences)

    @Test
    fun `invoke delegates to the preferences layer`() = runTest {
        saveAppEntry()

        assertEquals(1, preferences.saveAppEntryCallCount)
    }

    @Test
    fun `invoke marks onboarding as completed`() = runTest {
        saveAppEntry()

        assertTrue(preferences.readAppEntry().first())
    }

    @Test
    fun `invoke is idempotent`() = runTest {
        saveAppEntry()
        saveAppEntry()
        saveAppEntry()

        // Three writes, still a single "already onboarded" state — a user who taps Get Started
        // twice must not end up in a different place from one who taps it once.
        assertEquals(3, preferences.saveAppEntryCallCount)
        assertTrue(preferences.readAppEntry().first())
    }

    @Test
    fun `no write happens until invoke is called`() = runTest {
        // Constructing the use case must not touch storage; onboarding would be skipped forever.
        assertEquals(0, preferences.saveAppEntryCallCount)
    }
}
