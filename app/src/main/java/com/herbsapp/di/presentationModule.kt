package com.herbsapp.di

import com.herbsapp.presentation.viewmodels.AuthViewModel
import com.herbsapp.presentation.viewmodels.DeterminerViewModel
import com.herbsapp.presentation.viewmodels.FlowerInfoViewModel
import com.herbsapp.presentation.viewmodels.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val presentationModule = module {
    factory<MainViewModel> { MainViewModel(get(), get()) }
    factory<FlowerInfoViewModel> { FlowerInfoViewModel(get()) }
    factory<DeterminerViewModel> { DeterminerViewModel(get(),get()) }
    factory<AuthViewModel> { AuthViewModel(get()) }
}