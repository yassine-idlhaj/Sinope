package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.model.Account
import com.example.sinope.domain.repository.account.IAccountRepository

class InsertAccount(
    private val repository: IAccountRepository
){

    suspend operator fun invoke(account: Account){
        repository.insertAccount(account)
    }
}