package com.example.sinope.di

import android.app.Application
import com.example.sinope.data.app_manager.LocalUserPreferencesImpl
import com.example.sinope.domain.repository.app_manager.ILocalUserPreferences
import com.example.sinope.domain.usecases.app_entry.AppEntryUseCase
import com.example.sinope.domain.usecases.app_entry.ReadAppEntry
import com.example.sinope.domain.usecases.app_entry.SaveAppEntry
import com.example.sinope.domain.usecases.language.LanguageUseCases
import com.example.sinope.domain.usecases.language.ReadLanguage
import com.example.sinope.domain.usecases.language.SaveLanguage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModuleDI {


    @Provides
    @Singleton
    fun provideLocalUserPreferences(
        application: Application
    ): ILocalUserPreferences = LocalUserPreferencesImpl(context = application)


    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserPreferences: ILocalUserPreferences
    ): AppEntryUseCase = AppEntryUseCase(
        readAppEntry = ReadAppEntry(localUserPreferences),
        saveAppEntry = SaveAppEntry(localUserPreferences)
    )

    @Provides
    @Singleton
    fun provideLanguageUseCases(
        localUserPreferences: ILocalUserPreferences
    ): LanguageUseCases = LanguageUseCases(
        saveLanguage = SaveLanguage(localUserPreferences),
        readLanguage = ReadLanguage(localUserPreferences)
    )



}