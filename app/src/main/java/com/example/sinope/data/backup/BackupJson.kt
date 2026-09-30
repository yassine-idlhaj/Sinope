package com.example.sinope.data.backup

import kotlinx.serialization.json.Json

/** Shared JSON config for everything written to / read from a .sinope file. */
val BackupJson = Json {
    // Write every field, even defaults, so the file means exactly what it says.
    encodeDefaults = true
    // A file from a newer Sinope with extra fields still parses.
    ignoreUnknownKeys = true
}
