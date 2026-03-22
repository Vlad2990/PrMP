package com.example.app.di

import com.example.app.data.SystemApiProvider
import com.example.app.domain.entities.ExpressionEvaluator
import com.example.app.domain.entities.ExpressionFormatter
import com.example.app.domain.interfaces.SystemApiProviderInterface
import com.example.app.domain.usecase.AddBracketsUseCase
import com.example.app.domain.usecase.CalculateUseCase
import com.example.app.domain.usecase.ClearOnShakeUseCase
import com.example.app.domain.usecase.CopyToClipboardUseCase
import com.example.app.domain.usecase.ToggleSignUseCase
import com.example.app.domain.usecase.VibrateUseCase
import com.example.app.domain.usecase.WriteOperatorUseCase
import com.example.app.ui.modelview.CalculatorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::ExpressionEvaluator)
    singleOf(::ExpressionFormatter)

    singleOf(::SystemApiProvider) bind SystemApiProviderInterface::class

    factoryOf(::CalculateUseCase)
    factoryOf(::ToggleSignUseCase)
    factoryOf(::AddBracketsUseCase)
    factoryOf(::WriteOperatorUseCase)
    factoryOf(::CopyToClipboardUseCase)
    factoryOf(::VibrateUseCase)
    factoryOf(::ClearOnShakeUseCase)

    viewModelOf(::CalculatorViewModel)
}