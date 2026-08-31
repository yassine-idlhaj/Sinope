package com.example.sinope.domain.usecases.language

import com.example.sinope.domain.model.AppLanguage
import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences

class SaveLanguage(
    private val localUserPreferences: ILocalUserPreferences
) {

    suspend operator fun invoke(language: AppLanguage) {
        localUserPreferences.saveLanguage(language.name)
    }
}