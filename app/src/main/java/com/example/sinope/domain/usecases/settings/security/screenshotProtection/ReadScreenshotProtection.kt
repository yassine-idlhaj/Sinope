package com.example.sinope.domain.usecases.settings.security.screenshotProtection

import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences
import kotlinx.coroutines.flow.Flow

class ReadScreenshotProtection(
    private val localUserPreferences: ILocalUserPreferences
) {

    operator fun invoke(): Flow<Boolean>{
        return localUserPreferences.readScreenshotProtection()
    }
}