package com.example.sinope.domain.usecases.account

import com.example.sinope.domain.repository.account.IAccountRepository

class ToggleFavorite(
    private val repository: IAccountRepository
) {
    suspend operator fun invoke(
        id:Long,
        favorite: Boolean
    ){
        repository.updateFavorite(id, favorite)
    }
}