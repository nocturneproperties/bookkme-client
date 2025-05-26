package me.bookk.feature.business.presentation.di

import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.create.CreateBusinessViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformBusinessDiModule(): Module = module {
    viewModelOf(::CreateBusinessViewModel)
    viewModelOf(::BusinessBootstrapViewModel)
}