package com.example.sinope.data.backup.repository

import android.content.Context
import androidx.core.net.toUri
import com.example.sinope.data.backup.BackupJson
import com.example.sinope.data.backup.crypto.BackupCrypto
import com.example.sinope.data.backup.mapper.toBackupAccount
import com.example.sinope.data.backup.mapper.toDomain
import com.example.sinope.data.backup.model.BackupPayload
import com.example.sinope.data.backup.model.SinopeBackupFile
import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.backup.IBackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class BackupRepositoryImpl(
    private val context: Context,
    private val backupCrypto: BackupCrypto,
) : IBackupRepository {

    companion object {
        // Far larger than any real vault; read before parsing so a huge file can't exhaust memory.
        private const val MAX_FILE_BYTES = 5 * 1024 * 1024
    }

    override suspend fun exportBackup(
        accounts: List<Account>,
        password: CharArray,
        destinationUri: String,
    ) {
        // Argon2id is CPU-heavy work, so Default (not IO).
        val fileBytes = withContext(Dispatchers.Default) {
            val payload = BackupPayload(
                version = BackupPayload.CURRENT_VERSION,
                createdAt = System.currentTimeMillis(),
                accounts = accounts.map { it.toBackupAccount() },
            )

            // Plaintext only ever exists in memory; wiped as soon as it's encrypted.
            val plaintext = BackupJson
                .encodeToString(BackupPayload.serializer(), payload)
                .toByteArray(Charsets.UTF_8)

            val file = try {
                backupCrypto.encrypt(plaintext, password)
            } finally {
                plaintext.fill(0)
            }

            BackupJson
                .encodeToString(SinopeBackupFile.serializer(), file)
                .toByteArray(Charsets.UTF_8)
        }

        // Only the encrypted container touches the disk.
        withContext(Dispatchers.IO) {
            val output = context.contentResolver.openOutputStream(destinationUri.toUri(), "wt")
                ?: throw IOException("Cannot open backup destination")
            output.use { it.write(fileBytes) }
        }
    }

    override suspend fun readBackup(sourceUri: String, password: CharArray): List<Account> {
        val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(sourceUri.toUri())?.use { input ->
                val data = input.readAtMost(MAX_FILE_BYTES + 1)
                if (data.size > MAX_FILE_BYTES) throw IOException("Backup file too large")
                data
            } ?: throw IOException("Cannot open backup file")
        }

        return withContext(Dispatchers.Default) {
            val file = BackupJson.decodeFromString(SinopeBackupFile.serializer(), bytes.decodeToString())

            // Throws AEADBadTagException when the password is wrong or the file was edited.
            val plaintext = backupCrypto.decrypt(file, password)
            try {
                val payload = BackupJson.decodeFromString(BackupPayload.serializer(), plaintext.decodeToString())
                require(payload.version <= BackupPayload.CURRENT_VERSION) {
                    "Backup made by a newer version of Sinope"
                }
                payload.accounts.map { it.toDomain() }
            } finally {
                plaintext.fill(0)
            }
        }
    }
}

/**
 * Reads at most [limit] bytes, stopping early at end of stream.
 *
 * Hand-rolled rather than `InputStream.readNBytes`, which only exists from API 33 — on anything
 * older that call throws NoSuchMethodError, and this app ships to API 30.
 */
internal fun InputStream.readAtMost(limit: Int): ByteArray {
    val collected = ByteArrayOutputStream()
    val chunk = ByteArray(DEFAULT_BUFFER_SIZE)

    while (collected.size() < limit) {
        val read = read(chunk, 0, minOf(chunk.size, limit - collected.size()))
        if (read == -1) break
        collected.write(chunk, 0, read)
    }

    return collected.toByteArray()
}
