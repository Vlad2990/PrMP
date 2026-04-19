package com.example.app.domain.usecase.auth

import com.example.app.domain.interfaces.UserAuthRepositoryInterface

class RegisterUseCase(private val auth: UserAuthRepositoryInterface) {
    suspend operator fun invoke(email: String, password: String) : Boolean {
        return auth.createUser(email, password)
    }
}