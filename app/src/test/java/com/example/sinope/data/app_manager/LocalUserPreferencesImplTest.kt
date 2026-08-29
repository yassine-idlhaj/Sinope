package com.example.sinope.data.app_manager

import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * The one piece of the onboarding feature that talks to the Android framework. The fakes used
 * elsewhere assume DataStore behaves; this test is what makes that assumption safe — it exercises
 * the real `preferencesDataStore` delegate instead of a stand-in.
 *
 * Robolectric ships framework jars per API level and has none for the project's `targetSdk` yet.
 * Onboarding uses no API newer than 34, so pinning the level keeps these tests runnable on the JVM
 * without weakening what they cover.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class LocalUserPreferencesImplTest {

    private val context = RuntimeEnvironment.getApplication()
    private val preferences = LocalUserPreferencesImpl(context)

    /**
     * `preferencesDataStore` hands out a process-wide singleton, so a store written by one test
     * would otherwise leak into the next one and make results depend on method order. Clearing it
     * up front gives every test the "fresh install" state it claims to start from.
     */
    @Before
    fun startFromAFreshInstall() = runBlocking {
        context.dataStore.edit { it.clear() }
        Unit
    }

    @Test
    fun `unwritten app entry key defaults to false`() = runTest {
        // A missing key must fall back, not blow up: this is the very first read on a fresh
        // install, and an exception here would crash the app before onboarding ever appears.
        assertFalse(preferences.readAppEntry().first())
    }

    @Test
    fun `saved app entry is visible to a subsequent read`() = runTest {
        preferences.saveAppEntry()

        assertTrue(preferences.readAppEntry().first())
    }

    @Test
    fun `two repository instances observe the same store`() = runTest {
        // Not hypothetical: MainActivity builds its own LocalUserPreferencesImpl while Hilt
        // provides a separate singleton to the view-model. Both must see one flag, or the app
        // would save onboarding through one object and read `false` through the other.
        preferences.saveAppEntry()

        val other = LocalUserPreferencesImpl(context)

        assertTrue(other.readAppEntry().first())
    }

    @Test
    fun `repeated saves keep the flag true`() = runTest {
        preferences.saveAppEntry()
        preferences.saveAppEntry()

        assertTrue(preferences.readAppEntry().first())
    }
}
