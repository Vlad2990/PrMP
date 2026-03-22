package com.example.app.domain.interfaces

interface SystemApiProviderInterface {
    fun copyToClipboard(text: String)
    fun vibrate()
    fun startShakingListener(onShake: () -> Unit)
    fun stopShakingListener()
}