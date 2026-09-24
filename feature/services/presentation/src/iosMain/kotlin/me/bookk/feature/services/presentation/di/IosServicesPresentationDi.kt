package me.bookk.feature.services.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.services.presentation.group.add.AddGroupViewModel
import me.bookk.feature.services.presentation.group.list.ServiceGroupListViewModel
import me.bookk.feature.services.presentation.service.add.AddServiceViewModel
import me.bookk.feature.services.presentation.service.list.ServiceListViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformServicesDiModule(): Module = module {
    factory { ServiceGroupListViewModel(get(), get(), get(), get(), get()) }
    factory { ServiceListViewModel(get(), get(), get(), get(), get()) }
    factory { AddServiceViewModel(it.get(), get(), get(), get(),  get(), get()) }
    factory { AddGroupViewModel(get(), get(), get(), get()) }
}

@UsedInSwift
fun serviceListVM(): ServiceListViewModel =
    KoinPlatform.getKoin().get()

@UsedInSwift
fun addServiceVM(businessId: Uuid): AddServiceViewModel =
    KoinPlatform.getKoin().get { parametersOf(businessId) }

@UsedInSwift
fun serviceGroupListVM(): ServiceGroupListViewModel =
    KoinPlatform.getKoin().get()

@UsedInSwift
fun addGroupVM(): AddGroupViewModel =
    KoinPlatform.getKoin().get()
