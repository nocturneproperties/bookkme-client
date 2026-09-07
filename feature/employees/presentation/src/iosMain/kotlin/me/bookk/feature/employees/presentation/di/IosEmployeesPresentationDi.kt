package me.bookk.feature.employees.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.employees.presentation.screen.invite.InviteEmployeeViewModel
import me.bookk.feature.employees.presentation.screen.list.EmployeeListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformEmployeesDiModule(): Module = module {
    factory { EmployeeListViewModel(get(), get(), get(), get()) }
    factoryOf(::InviteEmployeeViewModel)
}

@UsedInSwift
fun employeeListVM(): EmployeeListViewModel =
    KoinPlatform.getKoin().get()

@UsedInSwift
fun inviteEmployeeVM(businessId: Uuid): InviteEmployeeViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })
