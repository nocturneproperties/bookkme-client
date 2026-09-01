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
    factoryOf(::EmployeeListViewModel)
    factoryOf(::InviteEmployeeViewModel)
}

@UsedInSwift
fun employeeListVM(id: Uuid): EmployeeListViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(id) })

@UsedInSwift
fun inviteEmployeeVM(businessId: Uuid): InviteEmployeeViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })
