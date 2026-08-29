package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.repository.account.IAccountRepository

class IsAccountExists(
    private val repository: IAccountRepository
) {

    suspend operator fun invoke(secret: String): Boolean{
        return repository.getAccountBySecret(secret) != null
    }
}