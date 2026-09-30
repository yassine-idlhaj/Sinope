package com.example.sinope.domain.usecases.settings.security.screenshotProtection

import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences

class SaveScreenshotProtection(
    private val localUserPreferences: ILocalUserPreferences
){

    suspend operator fun invoke(screenshotProtection: Boolean){
        localUserPreferences.saveScreenshotProtection(screenshotProtection)
    }
}