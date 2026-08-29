package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccounts(
    private val repository: IAccountRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.getAccounts()
}