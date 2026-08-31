package com.example.sinope.domain.usecases.language

import com.example.sinope.domain.model.AppLanguage
import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReadLanguage(
    private val localUserPreferences: ILocalUserPreferences
) {

    operator fun invoke(): Flow<AppLanguage>{
        return localUserPreferences.readLanguage()
            .map { value ->
                runCatching {
                    AppLanguage.valueOf(value)
                }.getOrDefault(AppLanguage.SYSTEM)
            }
    }
}