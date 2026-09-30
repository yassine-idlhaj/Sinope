package com.example.sinope.di

import android.app.Application
import com.example.sinope.data.backup.crypto.BackupCrypto
import com.example.sinope.data.backup.repository.BackupRepositoryImpl
import com.example.sinope.domain.repository.account.IAccountRepository
import com.example.sinope.domain.repository.backup.IBackupRepository
import com.example.sinope.domain.usecases.backup.BackupUseCases
import com.example.sinope.domain.usecases.backup.ExportBackup
import com.example.sinope.domain.usecases.backup.FindImportCandidates
import com.example.sinope.domain.usecases.backup.HasAccountsToExport
import com.example.sinope.domain.usecases.backup.ImportAccounts
import com.example.sinope.domain.usecases.backup.ReadBackupFile
import com.example.sinope.domain.usecases.backup.ValidateBackupPassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupModule {

    @Provides
    @Singleton
    fun provideBackupCrypto(): BackupCrypto = BackupCrypto()

    @Provides
    @Singleton
    fun provideBackupRepository(
        application: Application,
        backupCrypto: BackupCrypto,
    ): IBackupRepository = BackupRepositoryImpl(application, backupCrypto)

    @Provides
    fun provideBackupUseCases(
        accountRepository: IAccountRepository,
        backupRepository: IBackupRepository,
    ): BackupUseCases = BackupUseCases(
        hasAccountsToExport = HasAccountsToExport(accountRepository),
        validateBackupPassword = ValidateBackupPassword(),
        exportBackup = ExportBackup(accountRepository, backupRepository),
        readBackupFile = ReadBackupFile(backupRepository),
        findImportCandidates = FindImportCandidates(accountRepository),
        importAccounts = ImportAccounts(accountRepository),
    )
}
