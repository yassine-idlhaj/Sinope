package com.example.sinope.domain.repository.app_manager

import kotlinx.coroutines.flow.Flow

interface ILocalUserPreferences {

    suspend fun saveAppEntry()

    fun readAppEntry(): Flow<Boolean>

    suspend fun saveLanguage(language: String)

    fun readLanguage(): Flow<String>

    fun readBiometricLock() : Flow<Boolean>
    suspend fun saveBiometricLock(biometricLock: Boolean)

    fun readScreenshotProtection(): Flow<Boolean>
    suspend fun saveScreenshotProtection(screenshotProtection: Boolean)
}
