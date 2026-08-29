package com.example.sinope.di

import android.app.Application
import androidx.room3.Room
import com.example.sinope.data.account.dao.AccountDao
import com.example.sinope.data.account.repository.AccountRepositoryImpl
import com.example.sinope.data.database.AppDatabase
import com.example.sinope.data.database.MIGRATION_1_2
import com.example.sinope.domain.repository.account.IAccountRepository
import com.example.sinope.domain.usecases.account.AccountUseCases
import com.example.sinope.domain.usecases.account.DeleteAccount
import com.example.sinope.domain.usecases.account.GenerateTotpCode
import com.example.sinope.domain.usecases.account.GetAccount
import com.example.sinope.domain.usecases.account.GetAccounts
import com.example.sinope.domain.usecases.account.InsertAccount
import com.example.sinope.domain.usecases.account.IsAccountExists
import com.example.sinope.domain.usecases.account.ParseQrCode
import com.example.sinope.domain.usecases.account.ToggleFavorite
import com.example.sinope.domain.usecases.account.UpdateAccount
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(
      application: Application
    ): AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "sinope_db",
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideAccountDao(
        database: AppDatabase
    ): AccountDao = database.accountDao()

    @Provides
    @Singleton
    fun provideAccountRepository(accountDao: AccountDao): IAccountRepository =
        AccountRepositoryImpl(accountDao)


    @Provides
    fun provideAccountUseCase(
        repository: IAccountRepository
    ): AccountUseCases = AccountUseCases(
        getAccounts = GetAccounts(repository),
        getAccount = GetAccount(repository),
        insertAccount = InsertAccount(repository),
        updateAccount = UpdateAccount(repository),
        deleteAccount = DeleteAccount(repository),
        parseQrCode = ParseQrCode(),
        generateTotpCode = GenerateTotpCode(),
        toggleFavorite = ToggleFavorite(repository),
        isAccountExists = IsAccountExists(repository)
    )
}