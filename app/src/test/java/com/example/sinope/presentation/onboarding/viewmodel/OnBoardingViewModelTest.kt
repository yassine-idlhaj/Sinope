package com.example.sinope.presentation.onboarding.viewmodel

import com.example.sinope.domain.usecases.app_entry.AppEntryUseCase
import com.example.sinope.domain.usecases.app_entry.ReadAppEntry
import com.example.sinope.domain.usecases.app_entry.SaveAppEntry
import com.example.sinope.testing.FakeLocalUserPreferences
import com.example.sinope.testing.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * The view-model is the only place where an onboarding *event* becomes persisted *state*. These
 * tests cover that translation: the right event triggers the write, the write actually lands, and
 * nothing else is touched on the way.
 */
class OnBoardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferences = FakeLocalUserPreferences()
    private val viewModel = OnBoardingViewModel(
        AppEntryUseCase(
            readAppEntry = ReadAppEntry(preferences),
            saveAppEntry = SaveAppEntry(preferences),
        )
    )

    @Test
    fun `SaveAppEntry event persists the onboarding flag`() = runTest {
        viewModel.onEvent(OnBoardingEvent.SaveAppEntry)

        assertTrue(preferences.readAppEntry().first())
    }

    @Test
    fun `SaveAppEntry event writes exactly once`() = runTest {
        viewModel.onEvent(OnBoardingEvent.SaveAppEntry)

        assertEquals(1, preferences.saveAppEntryCallCount)
    }

    @Test
    fun `constructing the view model performs no IO`() = runTest {
        // A view-model is created on every configuration change. If it wrote or subscribed on
        // construction, rotating the phone mid-onboarding would end onboarding.
        assertEquals(0, preferences.saveAppEntryCallCount)
        assertEquals(0, preferences.readAppEntryCallCount)
        assertFalse(preferences.readAppEntry().first())
    }

    @Test
    fun `repeated SaveAppEntry events converge on the same state`() = runTest {
        repeat(5) { viewModel.onEvent(OnBoardingEvent.SaveAppEntry) }

        // Guards against a double-tap on "Get Started" corrupting the flag; the last write wins
        // and the flag stays true.
        assertEquals(5, preferences.saveAppEntryCallCount)
        assertTrue(preferences.readAppEntry().first())
    }
}
