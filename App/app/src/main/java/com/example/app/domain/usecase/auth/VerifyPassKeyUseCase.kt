package com.example.app.domain.usecase.auth

import com.example.app.domain.interfaces.UserAuthRepositoryInterface

class VerifyPassKeyUseCase(private val auth: UserAuthRepositoryInterface) {
    suspend operator fun invoke(pin: String): Boolean {
        return auth.verifyPassKey(pin)
    }
}