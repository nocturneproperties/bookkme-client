package me.bookk.feature.services.presentation.di

import me.bookk.feature.services.presentation.group.list.ServiceGroupListViewModel
import me.bookk.feature.services.presentation.service.add.AddServiceViewModel
import me.bookk.feature.services.presentation.service.list.ServiceListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal actual fun platformServicesDiModule(): Module = module {
    viewModel { ServiceListViewModel(it.get(), get(), get(), get()) }
    viewModel { AddServiceViewModel(it.get(), get(), get(), get(), get(),get()) }
    viewModel { ServiceGroupListViewModel(it.get(), get(), get(), get()) }
}
