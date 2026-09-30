package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.model.Account
import com.example.sinope.testing.FakeAccountRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The one irreversible action in the app, so the tests pin both halves of it: everything goes,
 * and anyone watching the vault is told it is now empty.
 */
class DeleteAllAccountsTest {

    private fun account(id: Long, issuer: String) = Account(
        id = id,
        issuer = issuer,
        accountName = "user$id@example.com",
        secret = "JBSWY3DPEHPK3PXP",
        algorithm = "SHA1",
        digits = 6,
        period = 30,
        emoji = "🔐",
        color = 0L,
        favorite = id == 1L,
    )

    private val vault = listOf(account(1, "GitHub"), account(2, "Google"), account(3, "Stripe"))

    @Test
    fun `removes every account, favourites included`() = runTest {
        val repository = FakeAccountRepository(vault)

        DeleteAllAccounts(repository)()

        assertTrue(repository.current.isEmpty())
    }

    @Test
    fun `reports how many were removed`() = runTest {
        val repository = FakeAccountRepository(vault)

        assertEquals(3, DeleteAllAccounts(repository)())
    }

    @Test
    fun `an already-empty vault is a no-op that removes nothing`() = runTest {
        val repository = FakeAccountRepository(emptyList())

        assertEquals(0, DeleteAllAccounts(repository)())
        assertTrue(repository.current.isEmpty())
    }

    @Test
    fun `observers of the vault see it go empty`() = runTest {
        // The settings stats card and the home list both read this flow, so the wipe has to
        // reach them rather than only the table.
        val repository = FakeAccountRepository(vault)
        assertEquals(3, repository.getAccounts().first().size)

        DeleteAllAccounts(repository)()

        assertEquals(emptyList<Account>(), repository.getAccounts().first())
    }

    @Test
    fun `deleting twice is safe`() = runTest {
        val repository = FakeAccountRepository(vault)
        val deleteAll = DeleteAllAccounts(repository)

        assertEquals(3, deleteAll())
        assertEquals(0, deleteAll())
    }
}
