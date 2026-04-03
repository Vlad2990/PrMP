package com.example.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.app.data.HistoryRepository
import com.example.app.data.SystemApiProvider
import com.example.app.data.ThemeRepository
import com.example.app.domain.entities.ExpressionEvaluator
import com.example.app.domain.entities.ExpressionFormatter
import com.example.app.domain.interfaces.HistoryRepositoryInterface
import com.example.app.domain.interfaces.SystemApiProviderInterface
import com.example.app.domain.interfaces.ThemeRepositoryInterface
import com.example.app.domain.usecase.AddBracketsUseCase
import com.example.app.domain.usecase.CalculateUseCase
import com.example.app.domain.usecase.ClearOnShakeUseCase
import com.example.app.domain.usecase.CopyToClipboardUseCase
import com.example.app.domain.usecase.GetHistoryUseCase
import com.example.app.domain.usecase.GetThemeUseCase
import com.example.app.domain.usecase.SaveToHistoryUseCase
import com.example.app.domain.usecase.SetThemeUseCase
import com.example.app.domain.usecase.ToggleSignUseCase
import com.example.app.domain.usecase.VibrateUseCase
import com.example.app.domain.usecase.WriteOperatorUseCase
import com.example.app.ui.modelview.CalculatorViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val appModule = module {
    singleOf(::ExpressionEvaluator)
    singleOf(::ExpressionFormatter)

    singleOf(::SystemApiProvider) bind SystemApiProviderInterface::class
    singleOf(::ThemeRepository) bind ThemeRepositoryInterface::class
    singleOf(::HistoryRepository) bind HistoryRepositoryInterface::class

    single<DataStore<Preferences>> {
        androidContext().dataStore
    }
    single<FirebaseFirestore> { Firebase.firestore }

    factoryOf(::CalculateUseCase)
    factoryOf(::ToggleSignUseCase)
    factoryOf(::AddBracketsUseCase)
    factoryOf(::WriteOperatorUseCase)
    factoryOf(::CopyToClipboardUseCase)
    factoryOf(::VibrateUseCase)
    factoryOf(::ClearOnShakeUseCase)
    factoryOf(::GetThemeUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::GetHistoryUseCase)
    factoryOf(::SaveToHistoryUseCase)

    viewModelOf(::CalculatorViewModel)
}