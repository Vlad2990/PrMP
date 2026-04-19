package com.example.app.domain.usecase.systemapi

import com.example.app.domain.interfaces.SystemApiProviderInterface

class VibrateUseCase(
    private val apiProvider: SystemApiProviderInterface
) {
    operator fun invoke() {
        apiProvider.vibrate()
    }
}