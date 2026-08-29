package com.example.sinope.domain.usecases.app_entry

import com.example.sinope.testing.FakeLocalUserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [ReadAppEntry] is what a future launch-router will consult to decide between onboarding and the
 * vault. The properties that matter are: a fresh install reads `false`, and the returned flow is
 * live (it re-emits when the flag is written) rather than a one-shot snapshot.
 */
class ReadAppEntryTest {

    private val preferences = FakeLocalUserPreferences()
    private val readAppEntry = ReadAppEntry(preferences)

    @Test
    fun `fresh install emits false`() = runTest {
        assertFalse(readAppEntry().first())
    }

    @Test
    fun `emits true once onboarding has been saved`() = runTest {
        preferences.saveAppEntry()

        assertTrue(readAppEntry().first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `flow stays live and re-emits when the flag changes`() = runTest {
        val emissions = mutableListOf<Boolean>()
        // Unconfined so collection is already running before the write below; on the default
        // `runTest` dispatcher the coroutine would not start until the test suspends, and the
        // first emission would be missed.
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            readAppEntry().take(2).toList(emissions)
        }

        preferences.setAppEntry(true)
        collector.join()

        // false (current state) then true (the write) — a cold snapshot would only ever yield one.
        assertEquals(listOf(false, true), emissions)
    }

    @Test
    fun `invoke does not collect eagerly`() = runTest {
        val flow = readAppEntry()

        // Obtaining the flow must be free; collection is the caller's decision. If the use case
        // collected here it would keep a coroutine alive for the whole process lifetime.
        assertEquals(1, preferences.readAppEntryCallCount)
        assertFalse(flow.first())
    }
}
