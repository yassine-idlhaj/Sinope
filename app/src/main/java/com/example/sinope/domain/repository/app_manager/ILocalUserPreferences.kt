package com.example.sinope.domain.repository.app_manager

import kotlinx.coroutines.flow.Flow

interface ILocalUserPreferences {

    suspend fun saveAppEntry()

    fun readAppEntry(): Flow<Boolean>

    suspend fun saveLanguage(language: String)

    fun readLanguage(): Flow<String>
}
