package com.example.sinope.domain.usecases.settings.security.biometricLock

import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences

class SaveBiometricLock(
    private val localUserPreferences: ILocalUserPreferences
) {


    suspend operator fun invoke(enable: Boolean){
        localUserPreferences.saveBiometricLock(enable)
    }
}