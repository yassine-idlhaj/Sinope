package com.example.sinope.domain.usecases.app_entry

import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences

class SaveAppEntry(
    private val localUserPreferences: ILocalUserPreferences
) {

    suspend operator fun invoke(){
        localUserPreferences.saveAppEntry();
    }
}