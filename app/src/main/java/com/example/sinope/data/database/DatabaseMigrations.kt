package com.example.sinope.data.database


import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE accounts
            ADD COLUMN emoji TEXT NOT NULL DEFAULT '🔐'
            """.trimIndent()
        )

        connection.execSQL(
            """
            ALTER TABLE accounts
            ADD COLUMN color INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )

        connection.execSQL(
            """
            ALTER TABLE accounts
            ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
    }


}