package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccount(
    private val repository: IAccountRepository
) {
    operator fun invoke(id: Long): Flow<Account?> = repository.getAccount(id)
}
