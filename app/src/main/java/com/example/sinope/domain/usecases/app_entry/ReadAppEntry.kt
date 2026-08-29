package com.example.sinope.domain.usecases.app_entry

import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences
import kotlinx.coroutines.flow.Flow

class ReadAppEntry(
    private val localUserPreferences: ILocalUserPreferences
) {
    operator fun invoke(): Flow<Boolean>{
        return localUserPreferences.readAppEntry()
    }
}