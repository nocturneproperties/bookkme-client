package me.bookk.feature.services.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.services.presentation.service.add.AddServiceViewModel
import me.bookk.feature.services.presentation.service.list.ServiceListViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformServicesDiModule(): Module = module {
    factory { ServiceListViewModel(it.get(), get(), get(), get()) }
    factory { AddServiceViewModel(it.get(), get(), get(), get(),  get(), get()) }
}

@UsedInSwift
fun serviceListVM(businessId: Uuid): ServiceListViewModel =
    KoinPlatform.getKoin().get { parametersOf(businessId) }

@UsedInSwift
fun addServiceVM(businessId: Uuid): AddServiceViewModel =
    KoinPlatform.getKoin().get { parametersOf(businessId) }