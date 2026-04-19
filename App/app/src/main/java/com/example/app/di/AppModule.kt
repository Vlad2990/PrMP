package com.example.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.app.data.repository.UserAuthRepositoryImpl
import com.example.app.data.HistoryRepositoryImpl
import com.example.app.data.SystemApiProviderImpl
import com.example.app.data.ThemeRepositoryImpl
import com.example.app.domain.entities.ExpressionEvaluator
import com.example.app.domain.entities.ExpressionFormatter
import com.example.app.domain.interfaces.*
import com.example.app.domain.usecase.auth.*
import com.example.app.domain.usecase.calculator.*
import com.example.app.domain.usecase.firestore.*
import com.example.app.domain.usecase.systemapi.*
import com.example.app.ui.modelview.CalculatorViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val appModule = module {

    single<DataStore<Preferences>> { androidContext().dataStore }
    single<FirebaseAuth> { Firebase.auth }
    single<FirebaseFirestore> { Firebase.firestore }

    singleOf(::SystemApiProviderImpl) bind SystemApiProviderInterface::class
    singleOf(::ThemeRepositoryImpl) bind ThemeRepositoryInterface::class
    singleOf(::HistoryRepositoryImpl) bind HistoryRepositoryInterface::class

    single<UserAuthRepositoryInterface> {
        UserAuthRepositoryImpl(get(), get())
    }
    factory { ExpressionEvaluator() }
    factory { ExpressionFormatter() }
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

    factoryOf(::UserRegisterCheckUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::VerifyPassKeyUseCase)
    factoryOf(::SetPassKeyUseCase)
    factoryOf(::PassKeySetCheckUseCase)
    factoryOf(::LoginUseCase)
    viewModelOf(::CalculatorViewModel)
}