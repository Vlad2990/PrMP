package com.example.app.domain.usecase.auth

import com.example.app.domain.interfaces.UserAuthRepositoryInterface

class SetPassKeyUseCase(private val auth: UserAuthRepositoryInterface) {
    suspend operator fun invoke(pin: String): Boolean {
        return auth.setupPassKey(pin)
    }
}