package me.bookk.feature.clients.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.clients.presentation.create.CreateClientViewModel
import me.bookk.feature.clients.presentation.details.ClientDetailsViewModel
import me.bookk.feature.clients.presentation.edit.EditClientViewModel
import me.bookk.feature.clients.presentation.list.ClientsListArgs
import me.bookk.feature.clients.presentation.list.ClientsListViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformClientsDiModule(): Module = module {
    factory { ClientsListViewModel(get(), it.get(), get(), get()) }
    factory { CreateClientViewModel(it.get(), get(), get(), get(), get(), get()) }
    factory { ClientDetailsViewModel(it.get(), get(), get(), get(), get()) }
    factory { EditClientViewModel(it.get(), get(), get(), get(), get(), get(), get(), get()) }
}

@UsedInSwift
fun clientsListVM(id: Uuid): ClientsListViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(ClientsListArgs(id)) })

@UsedInSwift
fun createClientVM(businessId: Uuid): CreateClientViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })

@UsedInSwift
fun createClientDetailsVM(id: Uuid): ClientDetailsViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(id) })

@UsedInSwift
fun editClientVM(id: Uuid): EditClientViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(id) })