package me.bookk.feature.business.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.clients.list.ClientsListArgs
import me.bookk.feature.business.presentation.clients.list.ClientsListViewModel
import me.bookk.feature.business.presentation.create.CreateBusinessViewModel
import me.bookk.feature.business.presentation.dashboard.BusinessDashboardViewModel
import me.bookk.feature.business.presentation.settings.BusinessSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformBusinessDiModule(): Module = module {
    factoryOf(::CreateBusinessViewModel)
    factoryOf(::BusinessBootstrapViewModel)
    factoryOf(::BusinessDashboardViewModel)
    factory { ClientsListViewModel(get(), it.get(), get(), get()) }
    factory { BusinessSettingsViewModel(it.get(), get(), get(), get(), get(), get()) }
}

@UsedInSwift
fun createBusinessVM(): CreateBusinessViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessBootstrapVM(): BusinessBootstrapViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessDashboardVM(): BusinessDashboardViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessSettingsVM(id: Uuid): BusinessSettingsViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(id) })

@UsedInSwift
fun clientsListVM(id: Uuid): ClientsListViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(ClientsListArgs(id)) })
