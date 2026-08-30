package me.bookk.feature.employees.presentation.di

import me.bookk.feature.employees.presentation.screen.invite.InviteEmployeeViewModel
import me.bookk.feature.employees.presentation.screen.list.EmployeeListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformEmployeesDiModule(): Module = module {
    viewModelOf(::EmployeeListViewModel)
    viewModelOf(::InviteEmployeeViewModel)
}
