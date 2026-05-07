package me.bookk.feature.clients.presentation.di

import me.bookk.feature.clients.presentation.create.CreateClientViewModel
import me.bookk.feature.clients.presentation.details.ClientDetailsViewModel
import me.bookk.feature.clients.presentation.list.ClientsListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal actual fun platformClientsDiModule(): Module = module {
    viewModel { ClientsListViewModel(get(), it.get(), get(), get()) }
    viewModel { CreateClientViewModel(it.get(), get(), get(), get()) }
    viewModel { ClientDetailsViewModel(it.get(), get(), get(), get(), get(), get()) }
}