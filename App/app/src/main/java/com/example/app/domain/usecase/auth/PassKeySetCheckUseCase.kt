package com.example.app.domain.usecase.auth

import com.example.app.domain.interfaces.UserAuthRepositoryInterface

class PassKeySetCheckUseCase(private val auth: UserAuthRepositoryInterface) {
    suspend operator fun invoke() : Boolean {
        return auth.isPassKeySet()
    }
}