package com.example.sinope.data.backup.crypto

import com.example.sinope.data.backup.model.CipherParams
import com.example.sinope.data.backup.model.KdfParams
import com.example.sinope.data.backup.model.SinopeBackupFile
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.nio.CharBuffer
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Password-based encryption for .sinope backups: Argon2id turns the password into a 256-bit key,
 * AES-256-GCM encrypts the payload.
 *
 * Independent from [com.example.sinope.data.security.CryptoManager]: the Keystore key never leaves
 * this device, while a backup must open on any device with just the password.
 *
 * The KDF cost is a constructor parameter so tests can run with cheap settings.
 */
class BackupCrypto(
    private val memoryKiB: Int = DEFAULT_MEMORY_KIB,
    private val iterations: Int = DEFAULT_ITERATIONS,
    private val parallelism: Int = DEFAULT_PARALLELISM,
    private val random: SecureRandom = SecureRandom(),
) {

    companion object {
        const val DEFAULT_MEMORY_KIB = 64 * 1024   // 64 MiB
        const val DEFAULT_ITERATIONS = 3
        const val DEFAULT_PARALLELISM = 1

        private const val SALT_BYTES = 16
        private const val NONCE_BYTES = 12
        private const val KEY_BYTES = 32
        private const val GCM_TAG_BITS = 128
        private const val TRANSFORMATION = "AES/GCM/NoPadding"

        // Upper bounds when reading a file, so a crafted header can't demand gigabytes of RAM.
        private const val MAX_MEMORY_KIB = 1024 * 1024
        private const val MAX_ITERATIONS = 20
        private const val MAX_PARALLELISM = 16
    }

    fun encrypt(plaintext: ByteArray, password: CharArray): SinopeBackupFile {
        val salt = randomBytes(SALT_BYTES)
        val nonce = randomBytes(NONCE_BYTES)

        val kdf = KdfParams(
            salt = salt.toBase64(),
            memoryKiB = memoryKiB,
            iterations = iterations,
            parallelism = parallelism,
        )
        val cipherParams = CipherParams(nonce = nonce.toBase64())

        val key = deriveKey(password, salt, kdf)
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_BITS, nonce))
            cipher.updateAAD(associatedData(SinopeBackupFile.FORMAT, SinopeBackupFile.CURRENT_VERSION, kdf, cipherParams))

            return SinopeBackupFile(
                kdf = kdf,
                cipher = cipherParams,
                ciphertext = cipher.doFinal(plaintext).toBase64(),
            )
        } finally {
            key.fill(0)
        }
    }

    /**
     * Reverse of [encrypt]. A wrong password or any edit to the file (header included) fails with
     * [javax.crypto.AEADBadTagException].
     */
    fun decrypt(file: SinopeBackupFile, password: CharArray): ByteArray {
        require(file.format == SinopeBackupFile.FORMAT) { "Not a Sinope backup" }
        require(file.version == SinopeBackupFile.CURRENT_VERSION) { "Unsupported backup version ${file.version}" }
        require(file.kdf.algorithm == KdfParams.ALGORITHM) { "Unsupported KDF ${file.kdf.algorithm}" }
        require(file.cipher.algorithm == CipherParams.ALGORITHM) { "Unsupported cipher ${file.cipher.algorithm}" }
        require(file.kdf.memoryKiB in 8..MAX_MEMORY_KIB) { "Invalid KDF memory" }
        require(file.kdf.iterations in 1..MAX_ITERATIONS) { "Invalid KDF iterations" }
        require(file.kdf.parallelism in 1..MAX_PARALLELISM) { "Invalid KDF parallelism" }

        val salt = file.kdf.salt.fromBase64()
        val nonce = file.cipher.nonce.fromBase64()
        require(nonce.size == NONCE_BYTES) { "Invalid nonce" }

        val key = deriveKey(password, salt, file.kdf)
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_BITS, nonce))
            cipher.updateAAD(associatedData(file.format, file.version, file.kdf, file.cipher))
            return cipher.doFinal(file.ciphertext.fromBase64())
        } finally {
            key.fill(0)
        }
    }

    private fun deriveKey(password: CharArray, salt: ByteArray, kdf: KdfParams): ByteArray {
        val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13)
            .withSalt(salt)
            .withMemoryAsKB(kdf.memoryKiB)
            .withIterations(kdf.iterations)
            .withParallelism(kdf.parallelism)
            .build()

        val generator = Argon2BytesGenerator().apply { init(params) }
        val passwordBytes = password.toUtf8Bytes()
        val key = ByteArray(KEY_BYTES)
        try {
            generator.generateBytes(passwordBytes, key)
        } finally {
            passwordBytes.fill(0)
        }
        return key
    }

    /**
     * Header fields bound to the ciphertext as GCM associated data: not encrypted, but any change
     * to them makes decryption fail. The ciphertext itself is already covered by the tag.
     */
    private fun associatedData(format: String, version: Int, kdf: KdfParams, cipher: CipherParams): ByteArray =
        listOf(
            format,
            version.toString(),
            kdf.algorithm,
            kdf.salt,
            kdf.memoryKiB.toString(),
            kdf.iterations.toString(),
            kdf.parallelism.toString(),
            cipher.algorithm,
            cipher.nonce,
        ).joinToString("|").toByteArray(Charsets.UTF_8)

    private fun randomBytes(size: Int) = ByteArray(size).also { random.nextBytes(it) }

    // Encodes without going through String, so the password bytes can be wiped afterwards.
    private fun CharArray.toUtf8Bytes(): ByteArray {
        val buffer = Charsets.UTF_8.encode(CharBuffer.wrap(this))
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        if (buffer.hasArray()) buffer.array().fill(0)
        return bytes
    }

    private fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)
    private fun String.fromBase64(): ByteArray = Base64.getDecoder().decode(this)
}
