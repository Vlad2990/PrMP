package com.example.app.domain.usecase.auth

import com.example.app.domain.interfaces.UserAuthRepositoryInterface

class UserRegisterCheckUseCase(private val auth: UserAuthRepositoryInterface) {
    suspend operator fun invoke(): Boolean {
        return auth.isUserLoggedIn()
    }
}