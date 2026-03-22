package com.example.app.domain.usecase

import com.example.app.domain.interfaces.SystemApiProviderInterface

class ClearOnShakeUseCase(
    private val apiProvider: SystemApiProviderInterface
) {
    fun execute(onShakeAction: () -> Unit) {
        apiProvider.startShakingListener {
            apiProvider.vibrate()
            onShakeAction()
        }
    }

    fun cleanup() {
        apiProvider.stopShakingListener()
    }
}