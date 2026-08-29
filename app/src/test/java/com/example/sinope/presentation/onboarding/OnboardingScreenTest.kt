package com.example.sinope.presentation.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sinope.presentation.onboarding.viewmodel.OnBoardingEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Behavioural tests for the onboarding flow's UI.
 *
 * `OnboardingScreen` owns its pager state internally, so there is no state object to assert on
 * from the outside — the only honest way to test it is to drive it the way a user does (taps and
 * swipes) and check what is on screen and which callbacks fire. That is what every test here does.
 *
 * These run on the JVM under Robolectric rather than on a device: the flow has no device-specific
 * behaviour, and keeping them in `test/` means they run in a normal `./gradlew test` and in CI
 * without an emulator. `GraphicsMode.NATIVE` is required because the screen draws its
 * illustrations and gradients through Compose's `Canvas`.
 *
 * The `qualifiers` pin a mid-size phone viewport (Pixel-class, 411x891dp). It is not cosmetic:
 * `assertIsDisplayed` fails on clipped content, and page two's four bullets do not fit on
 * Robolectric's much shorter default screen — the screen has no scroll container, so on a small
 * device the last guarantee really is cut off. Fixing the viewport keeps these tests about
 * behaviour; the layout limitation is called out separately in the report.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class OnboardingScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val events = mutableListOf<OnBoardingEvent>()
    private var finishCount = 0

    private fun launchOnboarding() {
        composeRule.setContent {
            OnboardingScreen(
                onFinish = { finishCount++ },
                onEvent = { events += it },
            )
        }
    }

    /** Taps "Next" [times] times, letting each page-change animation settle in between. */
    private fun advance(times: Int) = repeat(times) {
        composeRule.onNodeWithText("Next").performClick()
        composeRule.waitForIdle()
    }

    // ---------------------------------------------------------------- first page

    @Test
    fun `first page shows the welcome copy`() {
        launchOnboarding()

        composeRule.onNodeWithText("Welcome to Sinope").assertIsDisplayed()
        composeRule
            .onNodeWithText("Generate secure verification codes", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun `first page offers Skip and Next but no Back`() {
        launchOnboarding()

        composeRule.onNodeWithText("Skip").assertIsDisplayed()
        composeRule.onNodeWithText("Next").assertIsDisplayed()
        // There is nothing behind page one, so a Back affordance would be a dead end.
        composeRule.onNodeWithText("Back").assertDoesNotExist()
    }

    @Test
    fun `Skip reports completion immediately`() {
        launchOnboarding()

        composeRule.onNodeWithText("Skip").performClick()

        // The escape hatch for returning users: it must work from the very first frame, without
        // walking the remaining pages.
        assertEquals(1, finishCount)
    }

    // ---------------------------------------------------------------- navigation

    @Test
    fun `Next advances to the second page`() {
        launchOnboarding()

        advance(1)

        composeRule.onNodeWithText("Your codes. Your control.").assertIsDisplayed()
        composeRule.onNodeWithText("Welcome to Sinope").assertDoesNotExist()
    }

    @Test
    fun `Skip disappears once the user has moved past the welcome page`() {
        launchOnboarding()

        advance(1)

        // Skip is deliberately a first-page-only affordance; past that the pair Back/Next takes
        // over the same corner of the screen.
        composeRule.onNodeWithText("Skip").assertDoesNotExist()
    }

    @Test
    fun `Back returns to the previous page`() {
        launchOnboarding()
        advance(1)

        composeRule.onNodeWithText("Back").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Welcome to Sinope").assertIsDisplayed()
    }

    @Test
    fun `horizontal swipe moves between pages`() {
        launchOnboarding()

        composeRule.onNodeWithText("Welcome to Sinope").performTouchInput { swipeLeft() }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Your codes. Your control.").assertIsDisplayed()

        // Swiping is the primary gesture on a pager; it has to stay reversible.
        composeRule.onNodeWithText("Your codes. Your control.").performTouchInput { swipeRight() }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Welcome to Sinope").assertIsDisplayed()
    }

    // ---------------------------------------------------------------- page content

    @Test
    fun `second page lists every privacy guarantee`() {
        launchOnboarding()

        advance(1)

        // These bullets are the product promise Sinope is built on; a dropped one is a silent
        // regression that no crash or layout check would catch.
        listOf(
            "Stored locally",
            "No account required",
            "Works completely offline",
            "Export encrypted backups anytime",
        ).forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test
    fun `third page invites the user to add an account`() {
        launchOnboarding()

        advance(2)

        composeRule.onNodeWithText("Let's add your first account").assertIsDisplayed()
        composeRule.onNodeWithText("Scan a QR code", substring = true).assertIsDisplayed()
    }

    // ---------------------------------------------------------------- last page

    @Test
    fun `last page swaps Next for Get Started`() {
        launchOnboarding()

        advance(2)

        composeRule.onNodeWithText("Get Started").assertIsDisplayed()
        composeRule.onNodeWithText("Next").assertDoesNotExist()
    }

    @Test
    fun `last page hides Back so the primary action stands alone`() {
        launchOnboarding()

        advance(2)

        // Current, deliberate layout: the final call to action occupies the full width. Pinned
        // here so a change to `showBack` is a conscious decision rather than an accident.
        composeRule.onNodeWithText("Back").assertDoesNotExist()
    }

    @Test
    fun `Get Started emits SaveAppEntry`() {
        launchOnboarding()

        advance(2)
        composeRule.onNodeWithText("Get Started").performClick()
        composeRule.waitForIdle()

        // The whole point of the flow: finishing it must persist that the user has onboarded.
        assertEquals(listOf(OnBoardingEvent.SaveAppEntry), events)
    }

    @Test
    fun `no event is emitted before the flow is finished`() {
        launchOnboarding()

        advance(2)

        // Merely reaching the last page is not completion — the user can still swipe back.
        assertTrue(events.isEmpty())
    }

    @Test
    fun `Get Started does not go through the onFinish callback`() {
        launchOnboarding()

        advance(2)
        composeRule.onNodeWithText("Get Started").performClick()
        composeRule.waitForIdle()

        // Documents a real asymmetry in the current screen: "Skip" exits via `onFinish` while
        // "Get Started" only emits an event. A caller that navigates away on `onFinish` alone
        // will leave the user stranded on the last page — see the report accompanying these tests.
        assertEquals(0, finishCount)
    }
}
