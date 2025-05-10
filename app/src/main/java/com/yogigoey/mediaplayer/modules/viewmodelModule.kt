package com.yogigoey.mediaplayer.modules

import com.yogigoey.mediaplayer.feature.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel


val viewModelModule = module {
    viewModel { HomeViewModel(get(), androidApplication()) }
}