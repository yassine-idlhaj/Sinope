package com.example.sinope.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Swaps `Dispatchers.Main` for a [TestDispatcher] around each test.
 *
 * `viewModelScope` is hard-wired to `Dispatchers.Main`, which has no implementation on a plain JVM
 * and throws on first use. Installing a test dispatcher is what makes [OnBoardingViewModel]
 * testable off-device; [UnconfinedTestDispatcher] additionally runs launched coroutines eagerly, so
 * a test can assert on the effect of an event on the line right after dispatching it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
