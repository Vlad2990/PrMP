package com.example.app.domain.usecase.auth

import com.example.app.domain.interfaces.UserAuthRepositoryInterface

class LoginUseCase(private val auth: UserAuthRepositoryInterface) {
    suspend operator fun invoke(email: String, password: String) : Boolean {
        return auth.login(email, password)
    }
}