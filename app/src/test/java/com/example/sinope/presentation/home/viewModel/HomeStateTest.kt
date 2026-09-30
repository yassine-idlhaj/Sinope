package com.example.sinope.presentation.home.viewModel

import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.model.AccountUi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [HomeState.hasNoSearchResults] is what makes the screen explain an empty list rather than just
 * showing one, so it has to tell a failed search apart from an empty vault.
 */
class HomeStateTest {

    private val anAccount = AccountUi(
        id = "1",
        issuer = "GitHub",
        email = "dev@example.com",
        emoji = "🔐",
        code = "000 000",
        color = SinopeColors.Cyan,
        favorite = false,
        secondsLeft = 30,
        period = 30,
    )

    @Test
    fun `fires when a real query matched nothing`() {
        assertTrue(
            HomeState(accounts = emptyList(), searchQuery = "nothing here").hasNoSearchResults,
        )
    }

    @Test
    fun `stays quiet while the query still has matches`() {
        assertFalse(HomeState(accounts = listOf(anAccount), searchQuery = "git").hasNoSearchResults)
    }

    @Test
    fun `an empty vault is not a failed search`() {
        assertFalse(HomeState(accounts = emptyList(), searchQuery = "").hasNoSearchResults)
        assertFalse(HomeState(accounts = emptyList(), searchQuery = "   ").hasNoSearchResults)
    }
}
