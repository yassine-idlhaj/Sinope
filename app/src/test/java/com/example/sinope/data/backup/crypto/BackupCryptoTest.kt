package com.example.sinope.data.backup.crypto

import com.example.sinope.data.backup.BackupJson
import com.example.sinope.data.backup.model.SinopeBackupFile
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import javax.crypto.AEADBadTagException

class BackupCryptoTest {

    // Cheap KDF settings so the tests run fast; the format is identical.
    private val crypto = BackupCrypto(memoryKiB = 1024, iterations = 1, parallelism = 1)
    private val plaintext = """{"accounts":[{"secret":"JBSWY3DPEHPK3PXP"}]}""".toByteArray()

    @Test
    fun `encrypt then decrypt with the same password returns the original bytes`() {
        val file = crypto.encrypt(plaintext, "correct horse".toCharArray())

        assertArrayEquals(plaintext, crypto.decrypt(file, "correct horse".toCharArray()))
    }

    @Test
    fun `file survives a JSON round trip`() {
        val file = crypto.encrypt(plaintext, "correct horse".toCharArray())
        val json = BackupJson.encodeToString(SinopeBackupFile.serializer(), file)
        val parsed = BackupJson.decodeFromString(SinopeBackupFile.serializer(), json)

        assertEquals(file, parsed)
        assertArrayEquals(plaintext, crypto.decrypt(parsed, "correct horse".toCharArray()))
    }

    @Test(expected = AEADBadTagException::class)
    fun `wrong password fails`() {
        val file = crypto.encrypt(plaintext, "correct horse".toCharArray())
        crypto.decrypt(file, "wrong horse".toCharArray())
    }

    @Test(expected = AEADBadTagException::class)
    fun `edited header fails even with the right password`() {
        val file = crypto.encrypt(plaintext, "correct horse".toCharArray())
        val tampered = file.copy(kdf = file.kdf.copy(iterations = 2))
        crypto.decrypt(tampered, "correct horse".toCharArray())
    }

    @Test
    fun `two exports with the same password use different salt and nonce`() {
        val a = crypto.encrypt(plaintext, "correct horse".toCharArray())
        val b = crypto.encrypt(plaintext, "correct horse".toCharArray())

        assertNotEquals(a.kdf.salt, b.kdf.salt)
        assertNotEquals(a.cipher.nonce, b.cipher.nonce)
        assertNotEquals(a.ciphertext, b.ciphertext)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `absurd KDF memory in a crafted file is rejected before hashing`() {
        val file = crypto.encrypt(plaintext, "correct horse".toCharArray())
        crypto.decrypt(file.copy(kdf = file.kdf.copy(memoryKiB = Int.MAX_VALUE)), "correct horse".toCharArray())
    }
}
