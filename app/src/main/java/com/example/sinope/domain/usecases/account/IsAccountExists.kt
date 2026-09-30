package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.repository.account.IAccountRepository

class IsAccountExists(
    private val repository: IAccountRepository
) {

    suspend operator fun invoke(issuer: String,accountName:String): Boolean{
        return repository.accountExists(issuer,accountName)
    }
}