package com.example.sinope.domain.usecases.app_entry

import com.example.sinope.testing.FakeLocalUserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [AppEntryUseCase] is the bundle Hilt injects into the view-model. Its one real risk is a wiring
 * mistake — read and write ending up on different preference instances — which would make the app
 * appear to save onboarding while every read still returned `false`. This test walks the pair
 * end to end over a single backing store to rule that out.
 */
class AppEntryUseCaseTest {

    private val preferences = FakeLocalUserPreferences()
    private val useCase = AppEntryUseCase(
        readAppEntry = ReadAppEntry(preferences),
        saveAppEntry = SaveAppEntry(preferences),
    )

    @Test
    fun `read reflects what save wrote`() = runTest {
        assertFalse(useCase.readAppEntry().first())

        useCase.saveAppEntry()

        assertTrue(useCase.readAppEntry().first())
    }
}
