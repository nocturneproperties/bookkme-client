package me.bookk.feature.clients.presentation.di

import me.bookk.feature.clients.presentation.create.CreateClientViewModel
import me.bookk.feature.clients.presentation.details.ClientDetailsViewModel
import me.bookk.feature.clients.presentation.edit.EditClientViewModel
import me.bookk.feature.clients.presentation.list.ClientsListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformClientsDiModule(): Module = module {
    viewModelOf(::ClientsListViewModel)
    viewModelOf(::CreateClientViewModel)
    viewModelOf(::ClientDetailsViewModel)
    viewModelOf(::EditClientViewModel)
}
