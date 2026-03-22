package com.example.app.domain.usecase

import com.example.app.domain.interfaces.SystemApiProviderInterface

class CopyToClipboardUseCase(
    private val apiProvider: SystemApiProviderInterface
) {
    operator fun invoke(text: String) {
        if (text.isEmpty()) {
            return
        } else {
            apiProvider.copyToClipboard(text)
        }
    }
}