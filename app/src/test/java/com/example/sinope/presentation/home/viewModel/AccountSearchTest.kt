package com.example.sinope.presentation.home.viewModel

import com.example.sinope.domain.model.Account
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * The search filter is a pure function over domain accounts, so it is exercised here directly:
 * what matches, what doesn't, and that a blank query costs nothing.
 */
class AccountSearchTest {

    private fun account(id: Long, issuer: String, accountName: String) = Account(
        id = id,
        issuer = issuer,
        accountName = accountName,
        secret = "JBSWY3DPEHPK3PXP",
        algorithm = "SHA1",
        digits = 6,
        period = 30,
        emoji = "🔐",
        color = 0L,
        favorite = false,
    )

    private val vault = listOf(
        account(1, "GitHub", "dev@example.com"),
        account(2, "Google", "me@gmail.com"),
        account(3, "Société Générale", "client@sg.fr"),
    )

    private fun ids(query: String) = vault.filterBySearch(query).map { it.id }

    @Test
    fun `a blank query hands back the same list untouched`() {
        // assertSame: no copy, no allocation — this runs on every vault emission.
        assertSame(vault, vault.filterBySearch(""))
        assertSame(vault, vault.filterBySearch("   "))
    }

    @Test
    fun `matches the issuer regardless of case`() {
        assertEquals(listOf(1L), ids("git"))
        assertEquals(listOf(1L), ids("GITHUB"))
    }

    @Test
    fun `matches the account name too`() {
        assertEquals(listOf(2L), ids("gmail"))
        assertEquals(listOf(1L), ids("dev@"))
    }

    @Test
    fun `surrounding whitespace is ignored`() {
        assertEquals(listOf(1L), ids("  github  "))
    }

    @Test
    fun `accents fold both ways`() {
        // Typed on a plain keyboard, and typed with the accents the issuer actually uses.
        assertEquals(listOf(3L), ids("societe"))
        assertEquals(listOf(3L), ids("Générale"))
    }

    @Test
    fun `a query matching several accounts keeps them all, in vault order`() {
        // Both .com addresses, but not the .fr one.
        assertEquals(listOf(1L, 2L), ids("com"))
    }

    @Test
    fun `a query matching nothing yields nothing`() {
        assertEquals(emptyList<Long>(), ids("nothing here"))
    }
}
